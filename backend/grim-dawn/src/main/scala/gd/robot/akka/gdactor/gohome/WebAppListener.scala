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
import scala.util.{Failure, Success}

object WebAppListener {
  sealed trait GoHomeKey
  trait PreDo                                                                       extends GoHomeKey
  case object StartGoHomeKeyListener                                                extends PreDo
  case class StartActionComplete(isReady: Boolean)                                  extends PreDo
  sealed trait SendKey                                                              extends GoHomeKey
  case object PressGoHomeKeyBoard                                                   extends SendKey
  case object PressAutoEnableBuffBoard                                              extends SendKey
  case class RoundAction(replyTo: ActorRef[ActorRef[SkillsRoundAction3.GoHomeKey]]) extends SendKey
  case object StopWebSystem                                                         extends SendKey
  case object PressSkillRound                                                       extends SendKey

  def apply(): Behavior[GoHomeKey] = Behaviors.setup(new WebAppListener(_))
  def ready(): Behavior[GoHomeKey] = Behaviors.setup(new WebAppListenerImpl(_))
}

import WebAppListener._
class WebAppListener(context: ActorContext[GoHomeKey]) extends AbstractBehavior[GoHomeKey](context) {
  private val system                    = context.system
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  private implicit val scheduler        = system.classicSystem.scheduler
  private val logger                    = context.log

  override def onMessage(msg: GoHomeKey): Behavior[GoHomeKey] = {
    msg match {
      case s: PreDo =>
        s match {
          case StartGoHomeKeyListener =>
            def startAction = Future(GDHotKeyListener.startListen(context.self))(blockExecutionContext)
            context.pipeToSelf(startAction) {
              case Success(value) =>
                logger.info(s"Init project success.")
                StartActionComplete(true)
              case Failure(err) =>
                logger.error(s"Init project error.", err)
                StartActionComplete(false)
            }
            Behaviors.same
          case StartActionComplete(r) =>
            if (r)
              WebAppListener.ready()
            else
              Behaviors.same
        }
      case _: SendKey =>
        Behaviors.same
    }
  }
}

class WebAppListenerImpl(context: ActorContext[GoHomeKey]) extends AbstractBehavior[GoHomeKey](context) {
  private val system                    = context.system
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  private implicit val scheduler        = system.classicSystem.scheduler

  private val pressKeyboardActor: ActorRef[GoHomeKeyListener.GoHomeKey]  = context.spawnAnonymous(GoHomeKeyListener())
  private val imageSearcher: ActorRef[ImageSearcher.GoHomeKey]           = context.spawnAnonymous(ImageSearcher())
  private val skillsRoundAction2: ActorRef[SkillsRoundAction2.GoHomeKey] = context.spawnAnonymous(SkillsRoundAction2())
  private val logger                                                     = context.log

  private def stopWebSystem = {
    val closeAction = Future(GDHotKeyListener.close())(blockExecutionContext)
    closeAction.onComplete(_ => system.terminate())
  }

  private def delayMillions[T](million: Long, callable: Callable[Future[T]]): Future[T] = Patterns.after(
    Duration(million, MILLISECONDS),
    implicitly[CScheduler],
    implicitly[ExecutionContext],
    callable
  )

  override def onMessage(msg: GoHomeKey): Behavior[GoHomeKey] = {
    msg match {
      case _: PreDo => Behaviors.same
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
          case StopWebSystem =>
            stopWebSystem
            Behaviors.same
        }
    }
  }
}
