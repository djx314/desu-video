package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import gd.robot.akka.config.AppConfig
import gd.robot.akka.mainapp.GlobalVars
import javafx.scene.input.KeyCode

/** 技能输出循环 Actor
  */
object SkillsRoundAction2 {
  case class Skill(img: Array[Byte], message: List[ActionQueue.ActionStatus])

  trait GoHomeKey
  case object PressGoHomeKeyBoard extends GoHomeKey
  case object StartAction         extends GoHomeKey
  case object EndAction           extends GoHomeKey

  def apply(): Behavior[GoHomeKey] = Behaviors.setup(new SkillsRoundAction2(_))
}

import SkillsRoundAction2._
class SkillsRoundAction2(context: ActorContext[GoHomeKey]) extends AbstractBehavior[GoHomeKey](context) {
  private val system                    = context.system
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  private val self                      = context.self
  private val imageMatcher              = GlobalVars.imageMatcher
  private val gdSystemUtils             = GlobalVars.gdSystemUtils

  private val actionQueue: ActorRef[ActionQueue.ActionStatus] = context.spawnAnonymous(ActionQueue())

  private var enabled: Boolean        = false
  private var currentRunning: Boolean = false

  import ActionQueue._
  private def keyType(keyCode: KeyCode): Unit     = appendAction(KeyType(keyCode))
  private def delayAction(millions: Long): Unit   = appendAction(ActionInputDelay(millions))
  private def appendAction(a: ActionStatus): Unit = actionQueue ! a
  private def completeAction: Unit                = appendAction(ReplyTo(self, EndAction))

  private def lazyJineng(level: Int) = level match {
    case 0 =>
    case 1 =>
      delayAction(800)
    case 2 =>
      delayAction(1500)
    case 3 =>
      delayAction(2000)
    case _ =>
  }

  private def mouseRobot = {
    val action = for {
      _            <- gdSystemUtils.waitForFocus
      isMatch      <- imageMatcher.matchJineng
      matchImgs    <- imageMatcher.matchImgs
      level        <- imageMatcher.lantiaoPoint
      delayInfoOpt <- imageMatcher.matchDelay
    } yield {
      if (matchImgs.isEmpty) {
        delayInfoOpt match {
          case Some(delayInfo) =>
            lazyJineng(level)

            if (isMatch.is2) {
              keyType(KeyCode.Y)
              delayAction(100)
            }
            for (m <- delayInfo.message) appendAction(m)
          case None => delayAction(100)
        }
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
