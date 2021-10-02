package gd.robot.akka.gdactor

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import gd.robot.akka.config.AppConfig

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
  implicit val executionContext = context.system.dispatchers.lookup(AppConfig.gdSelector)

  val robot = new Robot

  def callRobotKeyPress(code: Int) = Future {
    robot.keyPress(code)
    robot.keyRelease(code)
  }(blockExecutionContext)

  override def onMessage(msg: CallRobotInput): Behavior[CallRobotInput] = {
    msg match {
      case KeyPressMessage(code, replyTo) =>
        val action = callRobotKeyPress(code)
        action.onComplete {
          case Success(value)     => replyTo ! InputRobotActor.ReplyKeyInput(code)
          case Failure(exception) => replyTo ! InputRobotActor.StopKeyInput
        }
    }
    Behaviors.same
  }
}
