package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import gd.robot.akka.config.AppConfig
import gd.robot.akka.utils.SystemRobot
import javafx.scene.input.KeyCode
import scalafx.geometry.Rectangle2D

object GoHomeKeyListener {
  trait GoHomeKey
  case object PressGoHomeKeyBoard extends GoHomeKey
  case object PressCanStart       extends GoHomeKey

  def apply(): Behavior[GoHomeKey] =
    Behaviors.setup(s => new GoHomeKeyListener(s, isNowWorking = false, s.spawnAnonymous(ActionQueue.init())))
}

import GoHomeKeyListener._
class GoHomeKeyListener(context: ActorContext[GoHomeKey], isNowWorking: Boolean, actionQueue: ActorRef[ActionQueue.ActionStatus])
    extends AbstractBehavior[GoHomeKey](context) {
  private val system                    = context.system
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  private val self                      = context.self

  import ActionQueue._
  private def appendAction(a: ActionStatus): Unit = actionQueue ! a

  private def keyPR(keyCode: KeyCode): Unit = appendAction(KeyType(keyCode))
  private def delayAction: Unit             = appendAction(ActionInputDelay(100))
  private def delayMouseAction: Unit        = appendAction(ActionInputDelay(500))
  private def completeAction: Unit          = appendAction(ReplyTo(self, PressCanStart))

  private def mouseRobot(bounds: Rectangle2D) = {
    appendAction(MouseMove((bounds.width / 2).toInt, (bounds.height / 2).toInt))
    delayAction
    appendAction(MouseClick)
    delayMouseAction
    keyPR(KeyCode.M)
    delayAction
    appendAction(MouseMove(956, 850))
    delayAction
    appendAction(MouseClick)
    delayMouseAction
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
          for (bounds <- SystemRobot.screenSize) mouseRobot(bounds)
        }
        new GoHomeKeyListener(context, !isNowWorking, actionQueue)
      case PressCanStart =>
        new GoHomeKeyListener(context, false, actionQueue)
    }
    Behaviors.same
  }
}
