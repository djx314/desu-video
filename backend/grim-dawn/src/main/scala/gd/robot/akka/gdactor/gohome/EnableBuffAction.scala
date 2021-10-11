package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import gd.robot.akka.config.AppConfig

import java.awt.event.KeyEvent

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
  def keyPR(keyCode: Int): Unit = {
    appendAction(KeyPressDown(keyCode))
    appendAction(KeyPressUp(keyCode))
  }
  def delayAction(millions: Long): Unit   = appendAction(ActionInputDelay(millions))
  def appendAction(a: ActionStatus): Unit = actionQueue ! a
  def completeAction: Unit                = appendAction(ReplyTo(self, PressCanStart))

  def mouseRobot = {
    keyPR(KeyEvent.VK_Y)
    delayAction(100)
    keyPR(KeyEvent.VK_1)
    delayAction(100)
    keyPR(KeyEvent.VK_2)
    delayAction(500)
    keyPR(KeyEvent.VK_3)
    delayAction(100)
    keyPR(KeyEvent.VK_4)
    delayAction(500)
    keyPR(KeyEvent.VK_5)
    delayAction(100)
    keyPR(KeyEvent.VK_Y)
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
