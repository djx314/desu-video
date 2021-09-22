package gd.robot.akka.gdactor

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import desu.video.akka.config.AppConfig

import java.awt.Robot
import scala.concurrent.Future
import scala.util.{Failure, Success}

object CallRobotActor {
  sealed trait CallRobotInput
  case class KeyPressMessage(keyCode: Int, replyTo: ActorRef[InputRobotActor.RobotInput]) extends CallRobotInput

  def apply(): Behavior[CallRobotInput] = Behaviors.setup(new CallRobotActor(_))
}

import CallRobotActor._
class CallRobotActor(context: ActorContext[CallRobotInput]) extends AbstractBehavior[CallRobotInput](context) {
  val blockExecutionContext     = context.system.dispatchers.lookup(DispatcherSelector.blocking())
  implicit val executionContext = context.system.dispatchers.lookup(DispatcherSelector.fromConfig(AppConfig.defaultDispatcherName))

  val robot = new Robot

  override def onMessage(msg: CallRobotInput): Behavior[CallRobotInput] = {
    msg match {
      case KeyPressMessage(code, replyTo) =>
        def keyPress   = Future(robot.keyPress(code))(blockExecutionContext)
        def keyRelease = Future(robot.keyRelease(code))(blockExecutionContext)
        val action = for {
          _ <- keyPress
          _ <- keyRelease
        } yield {}
        action.onComplete {
          case Success(value)     => replyTo ! InputRobotActor.ReplyKeyInput(code)
          case Failure(exception) => replyTo ! InputRobotActor.StopKeyInput
        }
    }
    Behaviors.same
  }
}
