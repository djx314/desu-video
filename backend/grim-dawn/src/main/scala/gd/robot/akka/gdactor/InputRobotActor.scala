package gd.robot.akka.gdactor

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import akka.pattern.Patterns
import gd.robot.akka.config.AppConfig

import java.util.concurrent.Callable
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object InputRobotActor {
  sealed trait RobotInput
  case class ReplyKeyInput(keyCode: Int)       extends RobotInput
  case class ReplyFailedKeyInput(keyCode: Int) extends RobotInput
  case class StartKeyInput(keyCode: Int)       extends RobotInput
  case object StopKeyInput                     extends RobotInput

  def apply(): Behavior[RobotInput] = Behaviors.setup(new InputRobotActor(_))
}

import InputRobotActor._
class InputRobotActor(context: ActorContext[RobotInput]) extends AbstractBehavior[RobotInput](context) {
  implicit val executionContext = context.system.dispatchers.lookup(DispatcherSelector.fromConfig(AppConfig.defaultDispatcherName))

  var isStarted: Boolean = false

  val sendTo = context.spawnAnonymous(CallRobotActor())

  override def onMessage(msg: RobotInput): Behavior[RobotInput] = {
    msg match {
      case StartKeyInput(code) =>
        sendTo ! CallRobotActor.KeyPressMessage(code, context.self)
        isStarted = true
        Behaviors.same
      case StopKeyInput =>
        isStarted = false
        Behaviors.stopped
      case ReplyKeyInput(code) =>
        val msg = StartKeyInput(code)
        val delayInput = Patterns.after(
          Duration(3200, MILLISECONDS),
          context.system.classicSystem.scheduler,
          executionContext,
          new Callable[Future[StartKeyInput]] {
            override def call(): Future[StartKeyInput] = Future.successful(msg)
          }
        )
        context.pipeToSelf(delayInput) {
          case Success(value)     => value
          case Failure(exception) => StopKeyInput
        }
        Behaviors.same
      case ReplyFailedKeyInput(code) =>
        context.self ! StopKeyInput
        Behaviors.same
    }
  }
}
