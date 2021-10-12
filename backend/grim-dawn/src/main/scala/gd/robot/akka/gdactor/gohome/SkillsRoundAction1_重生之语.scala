package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import gd.robot.akka.config.AppConfig
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
  val system                    = context.system
  val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  val self                      = context.self

  val actionQueue: ActorRef[ActionQueue.ActionStatus] = context.spawnAnonymous(ActionQueue())

  var enabled: Boolean     = false
  var currentRunCount: Int = 0

  import ActionQueue._
  def keyPR(keyCode: KeyCode): Unit = {
    appendAction(KeyPressDown(keyCode))
    appendAction(KeyPressUp(keyCode))
  }
  def delayAction(millions: Long): Unit   = appendAction(ActionInputDelay(millions))
  def appendAction(a: ActionStatus): Unit = actionQueue ! a
  def completeAction: Unit                = appendAction(ReplyTo(self, EndAction))

  def mouseRobot = {
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
          if (currentRunCount == 0) self ! StartAction
        } else {
          println("重生之语 close")
          enabled = false
        }
      case StartAction =>
        if (currentRunCount == 0 && enabled) {
          currentRunCount = 1
          mouseRobot
        }
      case EndAction =>
        if (currentRunCount > 1)
          currentRunCount -= 1
        else {
          if (enabled) mouseRobot
          else currentRunCount -= 1
        }
    }
    Behaviors.same
  }
}
