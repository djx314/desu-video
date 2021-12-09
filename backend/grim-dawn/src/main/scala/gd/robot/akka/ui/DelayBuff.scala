package gd.robot.akka.ui

import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, AskPattern, Behaviors}
import akka.util.Timeout
import gd.robot.akka.config.AppConfig
import gd.robot.akka.gdactor.gohome.{ActionQueue, WebAppListener}
import gd.robot.akka.mainapp.GlobalVars
import javafx.scene.input.KeyCode
import scalafx.beans.property.{BooleanProperty, DoubleProperty, ObjectProperty}

class DelayBuff(system: ActorSystem[Nothing], webAppListener: ActorRef[WebAppListener.GoHomeKey]) extends AutoCloseable {

  val isOn       = BooleanProperty(false)
  val delayTime  = DoubleProperty(0.05)
  val keyCodePro = ObjectProperty(KeyCode.DIGIT1)

  import AskPattern._
  import scala.concurrent.duration._
  implicit val scheduler        = system.scheduler
  implicit val timeout          = Timeout(3.seconds)
  implicit val executionContext = system.executionContext
  private val roundF = webAppListener ? ((replyTo: ActorRef[ActorRef[SkillsRoundAction3.GoHomeKey]]) => WebAppListener.RoundAction(replyTo))

  def tick() = {
    if (isOn.value) {
      for (roundActor <- roundF) {
        roundActor ! SkillsRoundAction3.ReStartAction(keyCodePro.value, (delayTime.value * 1000).toInt)
        roundActor ! SkillsRoundAction3.StartAction
      }
    } else {
      for (roundActor <- roundF) {
        roundActor ! SkillsRoundAction3.EndAction
      }
    }
  }

  override def close(): Unit = {
    for (roundActor <- roundF) {
      roundActor ! SkillsRoundAction3.ReleaseAction
    }
  }

}

object SkillsRoundAction3 {
  trait GoHomeKey
  case object StartAction                                 extends GoHomeKey
  case class ReStartAction(keyCode: KeyCode, delay: Long) extends GoHomeKey
  case object EndAction                                   extends GoHomeKey
  case object ReleaseAction                               extends GoHomeKey

  def apply(keyCode: KeyCode, delay: Long): Behavior[GoHomeKey] =
    Behaviors.setup(s => new SkillsRoundAction3(s, keyCode = keyCode, delay = delay))

  def apply2(): Behavior[GoHomeKey] = {
    Behaviors.receive((context, message) =>
      message match {
        case ReStartAction(keyCode, delay) => SkillsRoundAction3(keyCode, delay)
        case ReleaseAction                 => Behaviors.stopped
        case _                             => Behaviors.same
      }
    )
  }
}

import SkillsRoundAction3._
class SkillsRoundAction3(context: ActorContext[GoHomeKey], keyCode: KeyCode, delay: Long) extends AbstractBehavior[GoHomeKey](context) {
  private val system                    = context.system
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  private val self                      = context.self
  private val gdSystemUtils             = GlobalVars.gdSystemUtils

  private val actionQueue: ActorRef[ActionQueue.ActionStatus] = context.spawnAnonymous(ActionQueue())

  import ActionQueue._
  private def appendAction(a: ActionStatus): Unit = actionQueue ! a

  private def keyPR(keyCode: KeyCode): Unit     = appendAction(KeyType(keyCode))
  private def delayAction(millions: Long): Unit = appendAction(ActionInputDelay(millions))
  private def completeAction: Unit              = appendAction(ReplyTo(self, StartAction))

  private def mouseRobot = for (_ <- gdSystemUtils.waitForFocus) {
    keyPR(keyCode)
    delayAction(delay)
    completeAction
  }

  override def onMessage(msg: GoHomeKey): Behavior[GoHomeKey] = {
    msg match {
      case StartAction =>
        mouseRobot
        Behaviors.same
      case ReStartAction(keyCode1, delay1) =>
        SkillsRoundAction3(keyCode1, delay1)
      case EndAction =>
        SkillsRoundAction3.apply2()
      case ReleaseAction =>
        Behaviors.stopped
    }
  }
}
