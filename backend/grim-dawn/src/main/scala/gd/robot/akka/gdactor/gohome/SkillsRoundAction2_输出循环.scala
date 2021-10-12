package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import gd.robot.akka.config.AppConfig
import gd.robot.akka.utils.ImageMatcher
import javafx.scene.input.KeyCode

object SkillsRoundAction2 {
  case class Skill(img: Array[Byte], message: List[ActionQueue.ActionStatus])

  trait GoHomeKey
  case object PressGoHomeKeyBoard extends GoHomeKey
  case object StartAction         extends GoHomeKey
  case object EndAction           extends GoHomeKey

  def apply(imageMatcher: ImageMatcher): Behavior[GoHomeKey] = Behaviors.setup(s => new SkillsRoundAction2(s, imageMatcher))
}

import SkillsRoundAction2._
class SkillsRoundAction2(context: ActorContext[GoHomeKey], imageMatcher: ImageMatcher) extends AbstractBehavior[GoHomeKey](context) {
  val system                    = context.system
  val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  val self                      = context.self

  val actionQueue: ActorRef[ActionQueue.ActionStatus] = context.spawnAnonymous(ActionQueue())

  var enabled: Boolean        = false
  var currentRunning: Boolean = false

  import ActionQueue._
  def delayAction(millions: Long): Unit   = appendAction(ActionInputDelay(millions))
  def appendAction(a: ActionStatus): Unit = actionQueue ! a
  def completeAction: Unit                = appendAction(ReplyTo(self, EndAction))

  def mouseRobot = {
    val action = for (delayInfoOpt <- imageMatcher.matchDelay) yield {
      delayInfoOpt match {
        case Some(delayInfo) =>
          for (m <- delayInfo.message) {
            appendAction(m)
          }
        case None => delayAction(100)
      }
    }
    action.onComplete(_ => completeAction)
  }

  override def onMessage(msg: GoHomeKey): Behavior[GoHomeKey] = {
    msg match {
      case PressGoHomeKeyBoard =>
        if (enabled == false) {
          println("open")
          enabled = true
          if (!currentRunning) self ! StartAction
        } else {
          println("close")
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
