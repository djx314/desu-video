package gd.robot.akka.utils

import javafx.scene.image.WritableImage
import javafx.scene.robot.Robot
import org.bytedeco.javacpp._
import org.bytedeco.opencv.opencv_core._
import org.bytedeco.opencv.global.opencv_core._
import org.bytedeco.opencv.opencv_highgui._
import org.bytedeco.opencv.global.opencv_highgui._
import org.bytedeco.opencv.opencv_imgproc._
import org.bytedeco.opencv.global.opencv_imgproc._
import org.bytedeco.opencv.global.opencv_imgcodecs._

object ImageMatcher {

  lazy val 责难光环 = getClass.getResourceAsStream("责难光环.png").readAllBytes()

  lazy val 责难光环Img = cvIplImage(new Mat(new BytePointer(责难光环: _*)))

  def screenshotF = {
    val x1  = 500
    val y1  = 800
    val x2  = 900
    val y2  = 900
    val img = new WritableImage(x2 - x1, y2 - y1)
    new Robot().getScreenCapture(img, x1, y1, x2, y2)
  }

}
