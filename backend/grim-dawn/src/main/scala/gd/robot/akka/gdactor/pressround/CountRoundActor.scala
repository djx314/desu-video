package gd.robot.akka.gdactor.pressround

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import gd.robot.akka.config.AppConfig

import java.awt.Robot
import scala.concurrent.Future
import scala.util.{Failure, Success}

object CountRoundActor {
  sealed trait CallStartActor
  /*case class CallStartKeyPressSeq(press: List[CallStartKeyPress]) extends CallStartActor
  case class CallStartKeyPress(keyCode: Int, roundTime: Long)     extends CallStartActor
  case class NowDelayKeyPress(keyCode: Int, delayTime: Long)      extends CallStartActor
  case class NotNowPress(keyCode: Int)                            extends CallStartActor*/

  def apply(): Behavior[CallStartActor] = Behaviors.setup(new CountRoundActor(_))
}

import CountRoundActor._
class CountRoundActor(context: ActorContext[CallStartActor]) extends AbstractBehavior[CallStartActor](context) {
  val blockExecutionContext     = context.system.dispatchers.lookup(DispatcherSelector.blocking())
  implicit val executionContext = context.system.dispatchers.lookup(DispatcherSelector.fromConfig(AppConfig.defaultDispatcherName))

  /*var preAddToPlay: List[CallStartKeyPress] = List.empty

  var keyPressPlay: List[CallStartKeyPress] = List.empty
  var nowPress: List[NowDelayKeyPress]      = List.empty
  var notNowPress: List[NotNowPress]        = List.empty*/

  override def onMessage(msg: CallStartActor): Behavior[CallStartActor] = {
    /*msg match {
      case CallStartKeyPressSeq(press)            => keyPressPlay = keyPressPlay.appendedAll(press)
      case press @ CallStartKeyPress(code, round) => keyPressPlay = keyPressPlay.appended(press)
      case press @ NowDelayKeyPress(code, delay)  => nowPress = nowPress.appended(press)
      case press @ NotNowPress(code)              => notNowPress = notNowPress.appended(press)
    }*/
    Behaviors.same
  }
}
