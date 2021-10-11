package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import gd.robot.akka.config.AppConfig
import gd.robot.akka.utils.ImageMatcher
import javafx.scene.input.KeyCode
import org.bytedeco.opencv.opencv_core.IplImage

import scala.concurrent.Future
import scala.util.{Failure, Success}

object ImageSearcher {
  trait GoHomeKey
  case object PressGoHomeKeyBoard extends GoHomeKey
  case object PressCanStart       extends GoHomeKey

  def apply(): Behavior[GoHomeKey] = Behaviors.setup(s => new ImageSearcher(s))
}

import ImageSearcher._
class ImageSearcher(context: ActorContext[GoHomeKey]) extends AbstractBehavior[GoHomeKey](context) {
  val system                    = context.system
  val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  val self                      = context.self

  val actionQueue: ActorRef[ActionQueue.ActionStatus] = context.spawnAnonymous(ActionQueue())

  var isNowWorking: Boolean = false

  import ActionQueue._
  def keyPR(keyCode: KeyCode): Unit = {
    appendAction(KeyPressDown(keyCode))
    appendAction(KeyPressUp(keyCode))
  }
  def delayAction(millions: Long): Unit   = appendAction(ActionInputDelay(millions))
  def appendAction(a: ActionStatus): Unit = actionQueue ! a
  def completeAction: Unit                = appendAction(ReplyTo(self, PressCanStart))

  def mouseRobot = {
    keyPR(KeyCode.Y)
    delayAction(100)
    keyPR(KeyCode.DIGIT1)
    delayAction(100)
    keyPR(KeyCode.DIGIT2)
    delayAction(500)
    keyPR(KeyCode.DIGIT3)
    delayAction(100)
    keyPR(KeyCode.DIGIT4)
    delayAction(500)
    keyPR(KeyCode.DIGIT5)
    delayAction(100)
    keyPR(KeyCode.Y)
    completeAction
  }

  override def onMessage(msg: GoHomeKey): Behavior[GoHomeKey] = {
    msg match {
      case PressGoHomeKeyBoard =>
        def matchImg(iplImg: IplImage) = Future(ImageMatcher.matchImg(iplImg))(blockExecutionContext)
        val img = for {
          img     <- ImageMatcher.screenshotF(blockExecutionContext)
          ifMatch <- matchImg(img)
        } yield ifMatch

        img.onComplete {
          case Success(value)     => println(value)
          case Failure(exception) => exception.printStackTrace()
        }
      case PressCanStart =>
    }
    Behaviors.same
  }
}
