package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import gd.robot.akka.config.AppConfig
import gd.robot.akka.mainapp.GlobalVars
import javafx.scene.input.KeyCode

object EnableBuffAction {
  trait GoHomeKey
  case object PressGoHomeKeyBoard extends GoHomeKey
  case object PressCanStart       extends GoHomeKey

  def apply(): Behavior[GoHomeKey] = Behaviors.setup(s => new EnableBuffAction(s))
}

import EnableBuffAction._
class EnableBuffAction(context: ActorContext[GoHomeKey]) extends AbstractBehavior[GoHomeKey](context) {
  private val system                    = context.system
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  private val self                      = context.self
  private val gdSystemUtils             = GlobalVars.gdSystemUtils

  private val actionQueue: ActorRef[ActionQueue.ActionStatus] = context.spawnAnonymous(ActionQueue())

  private var isNowWorking: Boolean = false

  import ActionQueue._
  private def keyPR(keyCode: KeyCode): Unit       = appendAction(KeyType(keyCode))
  private def delayAction(millions: Long): Unit   = appendAction(ActionInputDelay(millions))
  private def appendAction(a: ActionStatus): Unit = actionQueue ! a
  private def completeAction: Unit                = appendAction(ReplyTo(self, PressCanStart))

  private def mouseRobot = {
    keyPR(KeyCode.Y)
    delayAction(100)
    keyPR(KeyCode.DIGIT1)
    delayAction(100)
    keyPR(KeyCode.DIGIT2)
    delayAction(500)
    keyPR(KeyCode.DIGIT3)
    delayAction(100)
    keyPR(KeyCode.DIGIT4)
    delayAction(500)
    keyPR(KeyCode.DIGIT5)
    delayAction(100)
    keyPR(KeyCode.Y)
    completeAction
  }

  override def onMessage(msg: GoHomeKey): Behavior[GoHomeKey] = {
    msg match {
      case PressGoHomeKeyBoard =>
        if (isNowWorking == false) {
          isNowWorking = true
          mouseRobot
          gdSystemUtils.isNowOnFocus.map(println)
        }
      case PressCanStart =>
        isNowWorking = false
    }
    Behaviors.same
  }
}
