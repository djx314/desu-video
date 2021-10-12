package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import gd.robot.akka.config.AppConfig
import gd.robot.akka.utils.ImageMatcher
import javafx.scene.input.KeyCode

import scala.concurrent.Future
import scala.util.{Failure, Success}

object ImageSearcher {
  case class PressJineng(keyCode: KeyCode, delay: Long)

  trait GoHomeKey
  case class InitActor(matcher: ImageMatcher)    extends GoHomeKey
  case object PressGoHomeKeyBoard                extends GoHomeKey
  case object ExecuteRunning                     extends GoHomeKey
  case class PressStart(list: List[PressJineng]) extends GoHomeKey
  case object PressCanStart                      extends GoHomeKey

  def apply(): Behavior[GoHomeKey] = Behaviors.setup(s => new ImageSearcher(s))
}

import ImageSearcher._
class ImageSearcher(context: ActorContext[GoHomeKey]) extends AbstractBehavior[GoHomeKey](context) {
  val system                    = context.system
  val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  val self                      = context.self

  val actionQueue: ActorRef[ActionQueue.ActionStatus] = context.spawnAnonymous(ActionQueue())

  var enabled: Boolean      = false
  var isNowWorking: Boolean = false

  var imgMatcher: ImageMatcher = null

  import ActionQueue._
  def keyPR(keyCode: KeyCode): Unit = {
    appendAction(KeyPressDown(keyCode))
    appendAction(KeyPressUp(keyCode))
  }
  def delayAction(millions: Long): Unit   = appendAction(ActionInputDelay(millions))
  def appendAction(a: ActionStatus): Unit = actionQueue ! a
  def completeAction: Unit                = appendAction(ReplyTo(self, PressCanStart))

  override def onMessage(msg: GoHomeKey): Behavior[GoHomeKey] = {
    msg match {
      case InitActor(matcher) => imgMatcher = matcher
      case PressGoHomeKeyBoard =>
        if (imgMatcher != null) {
          if (!enabled) {
            enabled = true
            if (!isNowWorking) {
              isNowWorking = true
              self ! ExecuteRunning
            }
          } else enabled = false
        }

      case ExecuteRunning =>
        val img = for {
          img     <- imgMatcher.screenshotF(blockExecutionContext)
          ifMatch <- Future(imgMatcher.matchImgs(img))
        } yield ifMatch

        img.onComplete {
          case Success(value) =>
            val list = value.map(s => PressJineng(keyCode = s.keyCode, delay = s.delay))
            if (list.isEmpty) completeAction
            else self ! PressStart(list)
          case Failure(exception) => exception.printStackTrace()
        }
      case PressStart(list) =>
        keyPR(KeyCode.Y)
        delayAction(100)
        for (l <- list) {
          keyPR(l.keyCode)
          delayAction(l.delay)
        }
        keyPR(KeyCode.Y)
        delayAction(2000)
        completeAction
      case PressCanStart =>
        if (enabled) self ! ExecuteRunning
        else isNowWorking = false

    }
    Behaviors.same
  }
}
