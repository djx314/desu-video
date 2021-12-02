package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import akka.http.scaladsl.Http.ServerBinding
import akka.pattern.Patterns
import gd.robot.akka.config.AppConfig
import gd.robot.akka.mainapp.MainApp
import gd.robot.akka.utils.ImageMatcher
import javafx.application.Platform
import javafx.scene.input.KeyCode

import java.util.concurrent.Callable
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.{Duration, MILLISECONDS}
import scala.util.{Failure, Success}

object WebAppListener {
  trait GoHomeKey
  case object StartGoHomeKeyListener               extends GoHomeKey
  case class StartActionComplete(isReady: Boolean) extends GoHomeKey
  case object PressGoHomeKeyBoard                  extends GoHomeKey
  case object PressEnableBuffBoard                 extends GoHomeKey
  case object PressAutoEnableBuffBoard             extends GoHomeKey
  case object RoundAction                          extends GoHomeKey
  case object ReadyToListen                        extends GoHomeKey
  case object StopWebSystem                        extends GoHomeKey
  case object PressSkillRound                      extends GoHomeKey

  def apply(binding: Future[ServerBinding]): Behavior[GoHomeKey] =
    Behaviors.setup(s => new WebAppListener(s, binding))
}

import WebAppListener._
class WebAppListener(context: ActorContext[GoHomeKey], binding: Future[ServerBinding]) extends AbstractBehavior[GoHomeKey](context) {
  private val system                    = context.system
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  private val self                      = context.self

  private val pressKeyboardActor: ActorRef[GoHomeKeyListener.GoHomeKey] = context.spawnAnonymous(GoHomeKeyListener())
  private val enableBuffAction: ActorRef[EnableBuffAction.GoHomeKey]    = context.spawnAnonymous(EnableBuffAction())
  private val imageSearcher: ActorRef[ImageSearcher.GoHomeKey]          = context.spawnAnonymous(ImageSearcher())
  private val 重生之语: ActorRef[SkillsRoundAction1.GoHomeKey] =
    context.spawnAnonymous(SkillsRoundAction1(keyCode = KeyCode.DIGIT6, delay = 15000))
  private val 蓝药: ActorRef[SkillsRoundAction1.GoHomeKey] = context.spawnAnonymous(SkillsRoundAction1(keyCode = KeyCode.R, delay = 27000))
  private val skillsRoundAction2: ActorRef[SkillsRoundAction2.GoHomeKey] = context.spawnAnonymous(SkillsRoundAction2())
  private val logger                                                     = context.log

  private var isReady: Boolean = false

  private def stopWebSystem = {
    val stopHotKey         = Future(GDHotKeyListener.close())(blockExecutionContext)
    val stopJavaFXPlatform = Future(Platform.exit())(blockExecutionContext)
    val unbindAction = for {
      bindingInstance <- binding
      _               <- bindingInstance.unbind()
    } yield {
      logger.info("Unbinded web app.")
    }
    val closeAction = for {
      _ <- stopHotKey
      _ <- stopJavaFXPlatform
      _ <- unbindAction
    } yield {
      logger.info("Close app source.")
    }
    closeAction.onComplete(_ => system.terminate())
  }

  // self ! StartGoHomeKeyListener

  private def delayMillions[T](million: Long, callable: Callable[Future[T]]): Future[T] = Patterns.after(
    Duration(million, MILLISECONDS),
    system.classicSystem.scheduler,
    implicitly[ExecutionContext],
    callable
  )

  override def onMessage(msg: GoHomeKey): Behavior[GoHomeKey] = {
    msg match {
      case StartGoHomeKeyListener =>
        def action1 = Future(GDHotKeyListener.startListen(self))(blockExecutionContext)
        def action2 = Future(Platform.startup(() => {}))(blockExecutionContext)
        val startAction = for {
          _ <- action1
          _ <- action2
        } yield {}
        context.pipeToSelf(startAction) {
          case Success(value) =>
            logger.info(s"Init project success.")
            StartActionComplete(true)
          case Failure(err) =>
            logger.error(s"Init project error.", err)
            StartActionComplete(false)
        }
      case StartActionComplete(r)   => isReady = r
      case PressGoHomeKeyBoard      => if (isReady) pressKeyboardActor ! GoHomeKeyListener.PressGoHomeKeyBoard
      case PressEnableBuffBoard     => if (isReady) enableBuffAction ! EnableBuffAction.PressGoHomeKeyBoard
      case PressAutoEnableBuffBoard => if (isReady) imageSearcher ! ImageSearcher.PressGoHomeKeyBoard
      case PressSkillRound          => if (isReady) skillsRoundAction2 ! SkillsRoundAction2.PressGoHomeKeyBoard
      case RoundAction =>
        if (isReady) {
          重生之语 ! SkillsRoundAction1.PressGoHomeKeyBoard
          def n = 蓝药 ! SkillsRoundAction1.PressGoHomeKeyBoard
          delayMillions(1000, () => Future(n))
        }
      case StopWebSystem => stopWebSystem
    }
    Behaviors.same
  }
}
