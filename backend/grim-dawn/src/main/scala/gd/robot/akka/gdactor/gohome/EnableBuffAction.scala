package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import gd.robot.akka.config.AppConfig
import gd.robot.akka.utils.SystemRobot

import java.awt.event.KeyEvent
import scala.concurrent.Future

object EnableBuffAction {
  trait GoHomeKey
  case object PressGoHomeKeyBoard extends GoHomeKey
  case object PressCanStart       extends GoHomeKey

  def apply(): Behavior[GoHomeKey] = Behaviors.setup(s => new EnableBuffAction(s))
}

import EnableBuffAction._
class EnableBuffAction(context: ActorContext[GoHomeKey]) extends AbstractBehavior[GoHomeKey](context) {
  val system                    = context.system
  val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  val self                      = context.self

  val actionQueue: ActorRef[ActionQueue.ActionStatus] = context.spawnAnonymous(ActionQueue())

  var isNowWorking: Boolean = false

  import ActionQueue._
  def deleyAction(a: => Unit, millions: Long): Unit = {
    appendAction(a)
    actionQueue ! ActionInputDelay(millions)
  }
  def appendAction(a: => Unit): Unit = actionQueue ! ActionInputCommon(() => Future(a)(blockExecutionContext))
  def completeAction: Unit = {
    def replyAction = Future(self ! PressCanStart)
    appendAction(replyAction)
  }

  def mouseRobot = {
    deleyAction(SystemRobot.keyPR(KeyEvent.VK_Y), 100)
    deleyAction(SystemRobot.keyPR(KeyEvent.VK_1), 100)
    deleyAction(SystemRobot.keyPR(KeyEvent.VK_2), 500)
    deleyAction(SystemRobot.keyPR(KeyEvent.VK_3), 100)
    deleyAction(SystemRobot.keyPR(KeyEvent.VK_4), 500)
    deleyAction(SystemRobot.keyPR(KeyEvent.VK_5), 100)
    appendAction(SystemRobot.keyPR(KeyEvent.VK_Y))
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
