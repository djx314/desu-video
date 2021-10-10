package gd.robot.akka.utils

import java.awt.Robot
import java.awt.event.InputEvent

object SystemRobot {

  val robot = new Robot()

  def keyPR(keyCode: Int): Unit = {
    robot.keyPress(keyCode)
    robot.keyRelease(keyCode)
  }

  def mouseClick: Unit = {
    mouseDown
    mouseUp
  }

  def mouseDown: Unit = robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)

  def mouseUp: Unit = robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)

  def mouseMove(x: Int, y: Int): Unit = robot.mouseMove(x, y)

}
