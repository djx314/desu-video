package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import gd.robot.akka.config.AppConfig
import gd.robot.akka.mainapp.GlobalVars
import javafx.scene.input.KeyCode

import scala.util.{Failure, Success}

object ImageSearcher {
  case class PressJineng(keyCode: KeyCode, delay: Long)

  trait GoHomeKey
  case object PressGoHomeKeyBoard                extends GoHomeKey
  case object ExecuteRunning                     extends GoHomeKey
  case class PressStart(list: List[PressJineng]) extends GoHomeKey
  case object PressCanStart                      extends GoHomeKey

  def apply(): Behavior[GoHomeKey] = Behaviors.setup(new ImageSearcher(_))
}

import ImageSearcher._
class ImageSearcher(context: ActorContext[GoHomeKey]) extends AbstractBehavior[GoHomeKey](context) {
  private val system                    = context.system
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)
  private val self                      = context.self
  private val imgMatcher                = GlobalVars.imageMatcher
  private val gdSystemUtils             = GlobalVars.gdSystemUtils

  private val actionQueue: ActorRef[ActionQueue.ActionStatus] = context.spawnAnonymous(ActionQueue.init())

  private var enabled: Boolean      = false
  private var isNowWorking: Boolean = false

  import ActionQueue._
  private def keyType(keyCode: KeyCode): Unit     = appendAction(KeyType(keyCode))
  private def delayAction(millions: Long): Unit   = appendAction(ActionInputDelay(millions))
  private def appendAction(a: ActionStatus): Unit = actionQueue ! a
  private def completeAction: Unit                = appendAction(ReplyTo(self, PressCanStart))

  override def onMessage(msg: GoHomeKey): Behavior[GoHomeKey] = {
    msg match {
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
        def directNextRound = {
          delayAction(100)
          completeAction
        }

        def execute(needRead: Boolean) =
          if (needRead) {
            val notMatch = imgMatcher.matchImgs
            notMatch.onComplete {
              case Success(value) =>
                val list = value.map(s => PressJineng(keyCode = s.keyCode, delay = s.delay))
                if (list.isEmpty) directNextRound
                else self ! PressStart(list)
              case Failure(exception) => exception.printStackTrace()
            }
          } else directNextRound

        val enableF = for {
          _      <- gdSystemUtils.waitForFocus
          result <- imgMatcher.imgEnabled
        } yield result

        enableF.map(execute)

      case PressStart(list) =>
        def sendKeyBoardMessage = {
          for (l <- list) {
            keyType(l.keyCode)
            delayAction(l.delay)
          }
          keyType(KeyCode.Y)
          delayAction(2000)
        }

        val action = for (isMatch <- imgMatcher.matchJineng) yield {
          if (isMatch.is1) {
            keyType(KeyCode.Y)
            delayAction(100)
          }
          sendKeyBoardMessage
        }

        action.onComplete(_ => completeAction)
      case PressCanStart =>
        if (enabled) self ! ExecuteRunning
        else isNowWorking = false

    }
    Behaviors.same
  }
}
