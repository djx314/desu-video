package gd.robot.akka.utils

import javafx.scene.input.{KeyCode, MouseButton}
import javafx.scene.robot.Robot
import scalafx.application.Platform
import scalafx.geometry.Rectangle2D
import scalafx.stage.Screen

import scala.concurrent.{Future, Promise}

object SystemRobot {

  def runJavafx(n: Robot => Unit): Future[Unit] = {
    val promise = Promise[Unit]()
    Platform.runLater(
      try {
        promise.trySuccess(n(new Robot))
      } catch {
        case e: Throwable =>
          promise.tryFailure(e)
      }
    )
    promise.future
  }
  def keyPress(keyCode: KeyCode): Future[Unit]   = runJavafx(robot => robot.keyPress(keyCode))
  def keyRelease(keyCode: KeyCode): Future[Unit] = runJavafx(robot => robot.keyRelease(keyCode))
  def keyType(keyCode: KeyCode): Future[Unit]    = runJavafx(robot => robot.keyType(keyCode))

  def mouseClick: Future[Unit]      = runJavafx(robot => robot.mouseClick(MouseButton.PRIMARY))
  def mouseRightClick: Future[Unit] = runJavafx(robot => robot.mouseClick(MouseButton.SECONDARY))
  def mouseDown: Future[Unit]       = runJavafx(robot => robot.mousePress(MouseButton.PRIMARY))
  def mouseUp: Future[Unit]         = runJavafx(robot => robot.mouseRelease(MouseButton.PRIMARY))

  def mouseMove(x: Int, y: Int): Future[Unit] = runJavafx(robot => robot.mouseMove(x, y))

  def screenSize: Future[Rectangle2D] = {
    val promise = Promise[Rectangle2D]()
    def laterSize = try {
      promise.trySuccess(Screen.primary.bounds)
    } catch {
      case e: Throwable => promise.tryFailure(e)
    }
    Platform.runLater(laterSize)
    promise.future
  }

}
