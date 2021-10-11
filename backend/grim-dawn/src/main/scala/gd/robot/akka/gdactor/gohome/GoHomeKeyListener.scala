package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import gd.robot.akka.config.AppConfig
import javafx.scene.input.KeyCode

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
  def keyPR(keyCode: KeyCode): Unit = {
    appendAction(KeyPressDown(keyCode))
    appendAction(KeyPressUp(keyCode))
  }
  def delayAction: Unit                   = appendAction(ActionInputDelay(100))
  def appendAction(a: ActionStatus): Unit = actionQueue ! a
  def completeAction: Unit                = appendAction(ReplyTo(self, PressCanStart))

  def mouseRobot = {
    keyPR(KeyCode.M)
    delayAction
    appendAction(MouseMove(956, 850))
    delayAction
    appendAction(MouseClick)
    delayAction
    for (_ <- 1 to 4) {
      appendAction(MouseMove(1310, 736))
      delayAction
      appendAction(MouseDown)
      delayAction
      appendAction(MouseMove(587, 273))
      delayAction
      appendAction(MouseUp)
    }
    appendAction(MouseMove(1176, 713))
    delayAction
    appendAction(MouseClick)
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
