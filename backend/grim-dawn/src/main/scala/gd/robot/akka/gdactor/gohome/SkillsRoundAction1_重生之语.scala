package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import gd.robot.akka.config.AppConfig
import gd.robot.akka.utils.SystemRobot

import java.awt.event.KeyEvent
import scala.concurrent.Future

object SkillsRoundAction1 {
  trait GoHomeKey
  case object PressGoHomeKeyBoard extends GoHomeKey
  case object StartAction         extends GoHomeKey
  case object EndAction           extends GoHomeKey

  def apply(keyCode: Int, delay: Long): Behavior[GoHomeKey] =
    Behaviors.setup(s => new SkillsRoundAction1(s, keyCode = keyCode, delay = delay))
}

import SkillsRoundAction1._
class SkillsRoundAction1(context: ActorContext[GoHomeKey], keyCode: Int, delay: Long) extends AbstractBehavior[GoHomeKey](context) {
  val system                    = context.system
  val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  val self                      = context.self

  val actionQueue: ActorRef[ActionQueue.ActionStatus] = context.spawnAnonymous(ActionQueue())

  var enabled: Boolean     = false
  var currentRunCount: Int = 0

  import ActionQueue._
  def deleyAction(a: => Unit, millions: Long): Unit = {
    appendAction(a)
    actionQueue ! ActionInputDelay(millions)
  }
  def appendAction(a: => Unit): Unit = actionQueue ! ActionInputCommon(() => Future(a)(blockExecutionContext))
  def completeAction: Unit = {
    def replyAction = Future(self ! EndAction)
    appendAction(replyAction)
  }

  def mouseRobot = {
    deleyAction(SystemRobot.keyPR(keyCode), delay)
    completeAction
  }

  override def onMessage(msg: GoHomeKey): Behavior[GoHomeKey] = {
    msg match {
      case PressGoHomeKeyBoard =>
        if (enabled == false) {
          enabled = true
          self ! StartAction
        } else enabled = false
      case StartAction =>
        if (currentRunCount == 0 && enabled) {
          currentRunCount = 1
          mouseRobot
        }
      case EndAction =>
        if (currentRunCount > 1)
          currentRunCount -= 1
        else if (enabled) mouseRobot
    }
    Behaviors.same
  }
}
