package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import akka.http.scaladsl.Http.ServerBinding
import akka.pattern.Patterns
import gd.robot.akka.config.AppConfig
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

  def apply(binding: Future[ServerBinding], appConfig: AppConfig): Behavior[GoHomeKey] =
    Behaviors.setup(s => new WebAppListener(s, binding, appConfig))
}

import WebAppListener._
class WebAppListener(context: ActorContext[GoHomeKey], binding: Future[ServerBinding], appConfig: AppConfig)
    extends AbstractBehavior[GoHomeKey](context) {
  val system                    = context.system
  val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  val self                      = context.self

  val pressKeyboardActor: ActorRef[GoHomeKeyListener.GoHomeKey] = context.spawnAnonymous(GoHomeKeyListener())
  val enableBuffAction: ActorRef[EnableBuffAction.GoHomeKey]    = context.spawnAnonymous(EnableBuffAction())
  val imageSearcher: ActorRef[ImageSearcher.GoHomeKey]          = context.spawnAnonymous(ImageSearcher(appConfig.imgMatch))
  val 重生之语: ActorRef[SkillsRoundAction1.GoHomeKey] = context.spawnAnonymous(SkillsRoundAction1(keyCode = KeyCode.DIGIT6, delay = 15000))
  val 蓝药: ActorRef[SkillsRoundAction1.GoHomeKey]   = context.spawnAnonymous(SkillsRoundAction1(keyCode = KeyCode.TAB, delay = 27000))
  val skillsRoundAction2: ActorRef[SkillsRoundAction2.GoHomeKey] = context.spawnAnonymous(SkillsRoundAction2(appConfig.imgMatch))

  var isReady: Boolean = false

  def stopWebSystem = {
    val stopHotKey         = Future(GDHotKeyListener.close())(blockExecutionContext)
    val stopJavaFXPlatform = Future(Platform.exit())(blockExecutionContext)
    val unbindAction = for {
      bindingInstance <- binding
      _               <- bindingInstance.unbind()
    } yield {}
    val closeAction = for {
      _ <- stopHotKey
      _ <- stopJavaFXPlatform
      _ <- unbindAction
    } yield {}
    closeAction.onComplete(_ => system.terminate())
  }

  self ! StartGoHomeKeyListener

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
          case Success(value) => StartActionComplete(true)
          case Failure(err)   => StartActionComplete(false)
        }
      case StartActionComplete(r)   => isReady = r
      case PressGoHomeKeyBoard      => if (isReady) pressKeyboardActor ! GoHomeKeyListener.PressGoHomeKeyBoard
      case PressEnableBuffBoard     => if (isReady) enableBuffAction ! EnableBuffAction.PressGoHomeKeyBoard
      case PressAutoEnableBuffBoard => if (isReady) imageSearcher ! ImageSearcher.PressGoHomeKeyBoard
      case PressSkillRound          => skillsRoundAction2 ! SkillsRoundAction2.PressGoHomeKeyBoard
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
