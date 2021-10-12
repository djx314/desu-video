package gd.robot.akka.utils

import javafx.application.Platform
import javafx.scene.input.{KeyCode, MouseButton}
import javafx.scene.robot.Robot

import scala.concurrent.{Future, Promise}

object SystemRobot {

  def runJavafx(n: Robot => Unit): Future[Unit] = {
    val promise = Promise[Unit]()
    Platform.runLater(() => {
      try {
        n(new Robot)
      } catch {
        case e: Exception =>
          e.printStackTrace()
      } finally {
        promise.trySuccess(())
      }
    })
    promise.future
  }
  def keyPress(keyCode: KeyCode): Future[Unit]   = runJavafx(robot => robot.keyPress(keyCode))
  def keyRelease(keyCode: KeyCode): Future[Unit] = runJavafx(robot => robot.keyRelease(keyCode))
  def keyPR(keyCode: KeyCode): Future[Unit]      = runJavafx(robot => robot.keyType(keyCode))

  def mouseClick: Future[Unit] = runJavafx(robot => robot.mouseClick(MouseButton.PRIMARY))
  def mouseDown: Future[Unit]  = runJavafx(robot => robot.mousePress(MouseButton.PRIMARY))
  def mouseUp: Future[Unit]    = runJavafx(robot => robot.mouseRelease(MouseButton.PRIMARY))

  def mouseMove(x: Int, y: Int): Future[Unit] = runJavafx(robot => robot.mouseMove(x, y))

}
