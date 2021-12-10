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
  trait GoHomeKey
  case object StartGoHomeKeyListener               extends GoHomeKey
  case class StartActionComplete(isReady: Boolean) extends GoHomeKey
  case object PressGoHomeKeyBoard                  extends GoHomeKey
  // case object PressEnableBuffBoard                 extends GoHomeKey
  case object PressAutoEnableBuffBoard                                              extends GoHomeKey
  case class RoundAction(replyTo: ActorRef[ActorRef[SkillsRoundAction3.GoHomeKey]]) extends GoHomeKey
  case object ReadyToListen                                                         extends GoHomeKey
  case object StopWebSystem                                                         extends GoHomeKey
  case object PressSkillRound                                                       extends GoHomeKey

  def apply(): Behavior[GoHomeKey]                 = Behaviors.setup(s => new WebAppListener(s))
  def apply(isReady: Boolean): Behavior[GoHomeKey] = Behaviors.setup(s => new WebAppListener(s, isReady = isReady))
}

import WebAppListener._
class WebAppListener(context: ActorContext[GoHomeKey], isReady: Boolean = false) extends AbstractBehavior[GoHomeKey](context) {
  private val system                    = context.system
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  private implicit val scheduler        = system.classicSystem.scheduler
  private val self                      = context.self

  private val pressKeyboardActor: ActorRef[GoHomeKeyListener.GoHomeKey] = context.spawnAnonymous(GoHomeKeyListener())
  // private val enableBuffAction: ActorRef[EnableBuffAction.GoHomeKey]    = context.spawnAnonymous(EnableBuffAction())
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
      case StartGoHomeKeyListener =>
        def startAction = Future(GDHotKeyListener.startListen(self))(blockExecutionContext)
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
        WebAppListener(r)
      case PressGoHomeKeyBoard =>
        if (isReady) pressKeyboardActor ! GoHomeKeyListener.PressGoHomeKeyBoard
        Behaviors.same
      // case PressEnableBuffBoard     => if (isReady) enableBuffAction ! EnableBuffAction.PressGoHomeKeyBoard
      case PressAutoEnableBuffBoard =>
        if (isReady) imageSearcher ! ImageSearcher.PressGoHomeKeyBoard
        Behaviors.same
      case PressSkillRound =>
        if (isReady) skillsRoundAction2 ! SkillsRoundAction2.PressGoHomeKeyBoard
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
