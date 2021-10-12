package gd.robot.akka.utils

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.WritableImage
import javafx.scene.input.KeyCode
import org.bytedeco.javacpp._
import org.bytedeco.opencv.opencv_core._
import org.bytedeco.opencv.global.opencv_core._
import org.bytedeco.opencv.opencv_highgui._
import org.bytedeco.opencv.global.opencv_highgui._
import org.bytedeco.opencv.opencv_imgproc._
import org.bytedeco.opencv.global.opencv_imgproc._
import org.bytedeco.opencv.global.opencv_imgcodecs._

import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Using

case class CompareImg(img: Array[Byte], keyCode: KeyCode, delay: Long)
case class CompareName(img: String, keyCode: KeyCode, delay: Long)

class ImageMatcher(val compareInfo: List[CompareImg]) {

  def screenshotF(blockingContext: ExecutionContext)(implicit ec: ExecutionContext): Future[Array[Byte]] = {
    val x1  = 500
    val y1  = 800
    val x2  = 900
    val y2  = 900
    val img = new WritableImage(x2 - x1, y2 - y1)
    val f   = SystemRobot.runJavafx(robot => robot.getScreenCapture(img, x1, y1, x2 - x1, y2 - y1))
    def write = Future {
      val io = new ByteArrayOutputStream
      ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", io)
      io.toByteArray
    }(blockingContext)
    for {
      _   <- f
      img <- write
    } yield img
  }

  def matchImg(compareImageByte: Array[Byte], screenshotByte: Array[Byte]): Boolean = {
    Using.resource(cvIplImage(ImageMatcher.loadMat(screenshotByte))) { source =>
      Using.resource(cvIplImage(ImageMatcher.loadMat(compareImageByte))) { compareImage =>
        def i =
          cvCreateImage(cvSize(source.width() - compareImage.width() + 1, source.height() - compareImage.height() + 1), IPL_DEPTH_32F, 1)
        Using.resource(i) { result =>
          cvZero(result)
          cvMatchTemplate(source, compareImage, result, CV_TM_CCORR_NORMED)
          val maxLoc: CvPoint = new CvPoint()
          val minLoc: CvPoint = new CvPoint()
          val minVal          = new DoublePointer(0d, 0d)
          val maxVal          = new DoublePointer(0d, 0d)

          cvMinMaxLoc(result, minVal, maxVal, minLoc, maxLoc, null)
          maxVal.get() > 0.99f
        }(cvReleaseImage)
      }
    }
  }

  def matchImgs(screenshot: Array[Byte]): List[CompareImg] = compareInfo.filter(s => !matchImg(compareImageByte = s.img, screenshot))

}

object ImageMatcher {
  def loadMat(arr: Array[Byte]): Mat = imdecode(new Mat(new BytePointer(arr: _*), false), IMREAD_ANYDEPTH | IMREAD_ANYCOLOR)

  def init(imgs: List[CompareName])(blockingec: ExecutionContext): Future[ImageMatcher] = {
    Future {
      val n = imgs.map { s =>
        val byte = getClass.getResourceAsStream(s.img).readAllBytes()
        CompareImg(byte, s.keyCode, s.delay)
      }
      new ImageMatcher(n)
    }(blockingec)
  }
}
