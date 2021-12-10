package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import gd.robot.akka.config.AppConfig
import gd.robot.akka.mainapp.GlobalVars
import gd.robot.akka.utils.{GDSystemUtils, ImageMatcher}
import javafx.scene.input.KeyCode

/** 技能输出循环 Actor
  */
object SkillsRoundAction2 {
  case class Skill(img: Array[Byte], message: List[ActionQueue.ActionStatus])

  trait GoHomeKey
  case object PressGoHomeKeyBoard extends GoHomeKey
  case object StartAction         extends GoHomeKey
  case object EndAction           extends GoHomeKey

  def apply(): Behavior[GoHomeKey] = Behaviors.setup(c => new SkillsRoundAction2(c, c.spawnAnonymous(ActionQueue.init())))
  def apply2(actionQueue: ActorRef[ActionQueue.ActionStatus]): Behavior[GoHomeKey] =
    Behaviors.setup(c => new SkillsRoundAction2(c, actionQueue))
}

import SkillsRoundAction2._
class SkillsRoundAction2(context: ActorContext[GoHomeKey], actionQueue: ActorRef[ActionQueue.ActionStatus])
    extends AbstractBehavior[GoHomeKey](context) {
  private val system                    = context.system
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)

  override def onMessage(msg: GoHomeKey): Behavior[GoHomeKey] = {
    msg match {
      case PressGoHomeKeyBoard =>
        context.self ! StartAction
        SkillsRoundAction2Inner(actionQueue)
      case _ =>
        Behaviors.same
    }
  }
}

object SkillsRoundAction2Inner {
  def apply(actionQueue: ActorRef[ActionQueue.ActionStatus]): Behavior[GoHomeKey] =
    Behaviors.setup(new SkillsRoundAction2Inner(_, actionQueue))
}

class SkillsRoundAction2Inner(context: ActorContext[GoHomeKey], actionQueue: ActorRef[ActionQueue.ActionStatus])
    extends AbstractBehavior[GoHomeKey](context) {
  private val system                    = context.system
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  private val imageMatcher              = GlobalVars[ImageMatcher]
  private val gdSystemUtils             = GlobalVars[GDSystemUtils]

  import ActionQueue._
  private def keyType(keyCode: KeyCode): Unit     = appendAction(KeyType(keyCode))
  private def delayAction(millions: Long): Unit   = appendAction(ActionInputDelay(millions))
  private def appendAction(a: ActionStatus): Unit = actionQueue ! a
  private def completeAction: Unit                = appendAction(ReplyTo(context.self, EndAction))

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
        SkillsRoundAction2.apply2(actionQueue)
      case StartAction =>
        mouseRobot
        Behaviors.same
      case EndAction =>
        context.self ! StartAction
        Behaviors.same
    }

  }
}
