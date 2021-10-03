package gd.robot.akka.gdactor.gohome

import akka.actor.typed.scaladsl._
import akka.actor.typed._
import akka.http.scaladsl.Http.ServerBinding
import akka.pattern.Patterns
import gd.robot.akka.config.AppConfig
import org.w3c.dom.events.MouseEvent

import java.awt.Robot
import java.awt.event.{InputEvent, KeyEvent}
import java.util.concurrent.Callable
import scala.concurrent.Future
import scala.concurrent.duration.{Duration, MILLISECONDS}
import scala.util.{Failure, Success}

object GoHomeKeyListener {
  trait GoHomeKey
  case object PressGoHomeKeyBoard extends GoHomeKey
  case object PressFinish         extends GoHomeKey

  def apply(): Behavior[GoHomeKey] = Behaviors.setup(s => new GoHomeKeyListener(s))
}

import GoHomeKeyListener._
class GoHomeKeyListener(context: ActorContext[GoHomeKey]) extends AbstractBehavior[GoHomeKey](context) {
  val system                    = context.system
  val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)

  var currentPress: Boolean = false

  def mouseRobot = {
    val robot = new Robot()
    val openMap = List(
      () => {
        robot.keyPress(KeyEvent.VK_M)
        robot.keyRelease(KeyEvent.VK_M)
      },
      () => robot.mouseMove(956, 850),
      () => {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
      }
    )
    val round = List(
      () => robot.mouseMove(1310, 736),
      () => robot.mousePress(InputEvent.BUTTON1_DOWN_MASK),
      () => robot.mouseMove(587, 273),
      () => robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
    )
    val click = List(
      () => robot.mouseMove(1176, 713),
      () => {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
      }
    )
    val actions = openMap ++: (1 to 4).flatMap(_ => round)
    val runAction = actions.map(s =>
      () =>
        Patterns.after(
          Duration(100, MILLISECONDS),
          context.system.classicSystem.scheduler,
          executionContext,
          new Callable[Future[Unit]] {
            override def call(): Future[Unit] = Future(s())(blockExecutionContext)
          }
        )
    )
    val n = runAction.reduce((a, b) => () => a().flatMap(_ => b()))
    n().flatMap(_ => Future(click.foreach(r => r()))(blockExecutionContext))
  }

  override def onMessage(msg: GoHomeKey): Behavior[GoHomeKey] = {
    msg match {
      case PressGoHomeKeyBoard =>
        if (currentPress == false) {
          currentPress = true
          context.pipeToSelf(mouseRobot) { _ =>
            PressFinish
          }
        }
      case PressFinish => currentPress = false
    }
    Behaviors.same
  }
}
