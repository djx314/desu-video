package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import akka.actor.{Scheduler => CScheduler}
import akka.pattern.Patterns
import gd.robot.akka.config.AppConfig
import gd.robot.akka.ui.SkillsRoundAction3

import java.util.concurrent.Callable
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.{Duration, MILLISECONDS}

object WebAppListener {
  sealed trait GoHomeKey
  sealed trait SendKey                                                              extends GoHomeKey
  case object PressGoHomeKeyBoard                                                   extends SendKey
  case object PressAutoEnableBuffBoard                                              extends SendKey
  case class RoundAction(replyTo: ActorRef[ActorRef[SkillsRoundAction3.GoHomeKey]]) extends SendKey
  case object PressSkillRound                                                       extends SendKey

  def apply(): Behavior[GoHomeKey] = Behaviors.setup(new WebAppListener(_))
}

import WebAppListener._

class WebAppListener(context: ActorContext[GoHomeKey]) extends AbstractBehavior[GoHomeKey](context) {
  private val system                    = context.system
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  private implicit val scheduler        = system.classicSystem.scheduler

  private val pressKeyboardActor: ActorRef[GoHomeKeyListener.GoHomeKey]  = context.spawnAnonymous(GoHomeKeyListener())
  private val imageSearcher: ActorRef[ImageSearcher.GoHomeKey]           = context.spawnAnonymous(ImageSearcher())
  private val skillsRoundAction2: ActorRef[SkillsRoundAction2.GoHomeKey] = context.spawnAnonymous(SkillsRoundAction2())

  private def delayMillions[T](million: Long, callable: Callable[Future[T]]): Future[T] = Patterns.after(
    Duration(million, MILLISECONDS),
    implicitly[CScheduler],
    implicitly[ExecutionContext],
    callable
  )

  override def onMessage(msg: GoHomeKey): Behavior[GoHomeKey] = {
    msg match {
      case s: SendKey =>
        s match {
          case PressGoHomeKeyBoard =>
            pressKeyboardActor ! GoHomeKeyListener.PressGoHomeKeyBoard
            Behaviors.same
          case PressAutoEnableBuffBoard =>
            imageSearcher ! ImageSearcher.PressGoHomeKeyBoard
            Behaviors.same
          case PressSkillRound =>
            skillsRoundAction2 ! SkillsRoundAction2.PressGoHomeKeyBoard
            Behaviors.same
          case RoundAction(replyTo) =>
            val actor = context.spawnAnonymous(SkillsRoundAction3())
            replyTo ! actor
            Behaviors.same
        }
    }
  }
}
