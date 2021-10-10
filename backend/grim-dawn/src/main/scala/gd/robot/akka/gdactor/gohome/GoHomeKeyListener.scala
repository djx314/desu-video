package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import gd.robot.akka.config.AppConfig
import gd.robot.akka.utils.SystemRobot

import java.awt.event.KeyEvent
import scala.concurrent.Future

object GoHomeKeyListener {
  trait GoHomeKey
  case object PressGoHomeKeyBoard extends GoHomeKey
  case object PressCanStart       extends GoHomeKey

  def apply(): Behavior[GoHomeKey] = Behaviors.setup(s => new GoHomeKeyListener(s))
}

import GoHomeKeyListener._
class GoHomeKeyListener(context: ActorContext[GoHomeKey]) extends AbstractBehavior[GoHomeKey](context) {
  val system                    = context.system
  val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  val self                      = context.self

  val actionQueue: ActorRef[ActionQueue.ActionStatus] = context.spawnAnonymous(ActionQueue())

  var isNowWorking: Boolean = false

  import ActionQueue._
  def deleyAction(a: => Unit): Unit = {
    appendAction(a)
    actionQueue ! ActionInputDelay(100)
  }
  def appendAction(a: => Unit): Unit = actionQueue ! ActionInputCommon(() => Future(a)(blockExecutionContext))
  def completeAction: Unit = {
    def replyAction = Future(self ! PressCanStart)
    appendAction(replyAction)
  }

  def mouseRobot = {
    deleyAction(SystemRobot.keyPR(KeyEvent.VK_M))
    deleyAction(SystemRobot.mouseMove(956, 850))
    deleyAction(SystemRobot.mouseClick)
    for (_ <- 1 to 4) {
      deleyAction(SystemRobot.mouseMove(1310, 736))
      deleyAction(SystemRobot.mouseDown)
      deleyAction(SystemRobot.mouseMove(587, 273))
      deleyAction(SystemRobot.mouseUp)
    }
    deleyAction(SystemRobot.mouseMove(1176, 713))
    appendAction(SystemRobot.mouseClick)
    completeAction
  }

  override def onMessage(msg: GoHomeKey): Behavior[GoHomeKey] = {
    msg match {
      case PressGoHomeKeyBoard =>
        if (isNowWorking == false) {
          isNowWorking = true
          mouseRobot
        }
      case PressCanStart =>
        isNowWorking = false
    }
    Behaviors.same
  }
}
