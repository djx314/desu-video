package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import gd.robot.akka.config.AppConfig
import gd.robot.akka.mainapp.GlobalVars
import javafx.scene.input.KeyCode

object SkillsRoundAction1 {
  trait GoHomeKey
  case object PressGoHomeKeyBoard extends GoHomeKey
  case object StartAction         extends GoHomeKey
  case object EndAction           extends GoHomeKey

  def apply(keyCode: KeyCode, delay: Long): Behavior[GoHomeKey] =
    Behaviors.setup(s => new SkillsRoundAction1(s, keyCode = keyCode, delay = delay))
}

import SkillsRoundAction1._
class SkillsRoundAction1(context: ActorContext[GoHomeKey], keyCode: KeyCode, delay: Long) extends AbstractBehavior[GoHomeKey](context) {
  private val system                    = context.system
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  private val self                      = context.self
  private val gdSystemUtils             = GlobalVars.gdSystemUtils

  private val actionQueue: ActorRef[ActionQueue.ActionStatus] = context.spawnAnonymous(ActionQueue())

  private var enabled: Boolean        = false
  private var currentRunning: Boolean = false

  import ActionQueue._
  private def keyPR(keyCode: KeyCode): Unit       = appendAction(KeyType(keyCode))
  private def delayAction(millions: Long): Unit   = appendAction(ActionInputDelay(millions))
  private def appendAction(a: ActionStatus): Unit = actionQueue ! a
  private def completeAction: Unit                = appendAction(ReplyTo(self, EndAction))

  private def mouseRobot = for (_ <- gdSystemUtils.waitForFocus) {
    keyPR(keyCode)
    delayAction(delay)
    completeAction
  }

  override def onMessage(msg: GoHomeKey): Behavior[GoHomeKey] = {
    msg match {
      case PressGoHomeKeyBoard =>
        if (enabled == false) {
          println("重生之语 open")
          enabled = true
          if (!currentRunning) self ! StartAction
        } else {
          println("重生之语 close")
          enabled = false
        }
      case StartAction =>
        if (!currentRunning && enabled) {
          currentRunning = true
          mouseRobot
        }
      case EndAction =>
        if (currentRunning) currentRunning = false
        if (enabled) self ! StartAction
    }
    Behaviors.same
  }
}
