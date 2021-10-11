package gd.robot.akka.utils

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.WritableImage
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

object ImageMatcher {

  def loadMat(arr: Array[Byte]): Mat = imdecode(new Mat(new BytePointer(arr: _*), false), IMREAD_ANYDEPTH | IMREAD_ANYCOLOR)

  lazy val 责难光环 = {
    try {
      getClass.getResourceAsStream("/责难光环.png").readAllBytes()
    } catch {
      case e: Exception =>
        e.printStackTrace()
        throw e
    }
  }

  lazy val 责难光环Img: IplImage = cvIplImage(loadMat(责难光环))

  def screenshotF(blockingContext: ExecutionContext)(implicit ec: ExecutionContext): Future[IplImage] = {
    val x1  = 500
    val y1  = 800
    val x2  = 900
    val y2  = 900
    val img = new WritableImage(x2 - x1, y2 - y1)
    val f   = SystemRobot.runJavafx(robot => robot.getScreenCapture(img, x1, y1, x2 - x1, y2 - y1))
    def write = Future {
      val io = new ByteArrayOutputStream
      ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", io)
      val arr = io.toByteArray
      ImageIO.write(
        SwingFXUtils.fromFXImage(img, null),
        "png",
        java.nio.file.Paths.get(".", "target", "1111111111111111111111111111111111.jpg").toFile
      )
      cvIplImage(loadMat(arr))
    }(blockingContext)
    for {
      _   <- f
      img <- write
    } yield img
  }

  def matchImg(screenshot: IplImage): Boolean = {
    Using.resource(screenshot) { source =>
      val i = cvCreateImage(cvSize(source.width() - 责难光环Img.width() + 1, source.height() - 责难光环Img.height() + 1), IPL_DEPTH_32F, 1)
      Using.resource(i) { result =>
        cvZero(result)
        cvMatchTemplate(source, 责难光环Img, result, CV_TM_CCORR_NORMED)
        val maxLoc: CvPoint = new CvPoint()
        val minLoc: CvPoint = new CvPoint()
        val minVal          = new DoublePointer(ArrayGen.gen: _*)
        val maxVal          = new DoublePointer(ArrayGen.gen: _*)

        cvMinMaxLoc(result, minVal, maxVal, minLoc, maxLoc, null)
        maxVal.get() > 0.99f
      }(cvReleaseImage)
    }
  }

}
