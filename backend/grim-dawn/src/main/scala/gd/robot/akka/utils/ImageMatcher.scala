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
import java.nio.file.{Files, Paths}
import javax.imageio.ImageIO
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.internal.util.FileUtils
import scala.util.Using

case class CompareImg(img: Array[Byte], keyCode: KeyCode, delay: Long)
case class JinengMatch(is1: Boolean, is2: Boolean, isZhandou: Boolean)

case class JavacvException(message: String, cause: Throwable) extends Exception(message, cause)

case class JinenglanImg(jineng1: Array[Byte], jineng2: Array[Byte], zhandou: Array[Byte])

class ImageMatcher(val compareInfo: List[CompareImg], val jinengImg: JinenglanImg, blockingContext: ExecutionContext) {

  def screenshotF(x1: Int, y1: Int, x2: Int, y2: Int)(implicit ec: ExecutionContext): Future[Array[Byte]] = {
    val img = new WritableImage(x2 - x1, y2 - y1)
    val f   = SystemRobot.runJavafx(robot => robot.getScreenCapture(img, x1, y1, x2 - x1, y2 - y1))
    def write = Future(
      Using.resource(new ByteArrayOutputStream) { io =>
        ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", io)
        io.toByteArray
      }
    )(blockingContext)
    for {
      _   <- f
      img <- write
    } yield img
  }

  def matchImg(compareImageByte: Array[Byte], screenshotByte: Array[Byte]): Boolean = try {
    Using.resources(cvIplImage(ImageMatcher.loadMat(screenshotByte)), cvIplImage(ImageMatcher.loadMat(compareImageByte))) {
      (source, compareImage) =>
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
  } catch {
    case t: Throwable => throw JavacvException("Error throws when compare image.", t)
  }

  def matchImgs(implicit ec: ExecutionContext): Future[List[CompareImg]] = {
    def compare(screenshot: Array[Byte]) = compareInfo.filter(s => !matchImg(compareImageByte = s.img, screenshot))
    for {
      screenshot <- screenshotF(x1 = 500, y1 = 800, x2 = 900, y2 = 900)
      result     <- Future(compare(screenshot))(blockingContext)
    } yield result
  }

  def matchJineng(implicit ec: ExecutionContext): Future[JinengMatch] = {
    def compare(compareImageByte: Array[Byte], screenshot: Array[Byte]) =
      Future(matchImg(compareImageByte = compareImageByte, screenshot))(blockingContext)
    for {
      screenshot <- screenshotF(x1 = 580, y1 = 920, x2 = 680, y2 = 1000)
      isZhandou  <- screenshotF(x1 = 850, y1 = 850, x2 = 900, y2 = 900)
      result1    <- compare(jinengImg.jineng1, screenshot)
      result2    <- compare(jinengImg.jineng2, screenshot)
      result3    <- compare(jinengImg.zhandou, isZhandou)
    } yield JinengMatch(is1 = result1, is2 = result2, isZhandou = result3)
  }

}

object ImageMatcher {
  def loadMat(arr: Array[Byte]): Mat = imdecode(new Mat(new BytePointer(arr: _*), false), IMREAD_ANYDEPTH | IMREAD_ANYCOLOR)
  def init(imgs: List[CompareImg], jinenglanImg: JinenglanImg)(blockingec: ExecutionContext): ImageMatcher =
    new ImageMatcher(imgs, jinenglanImg, blockingec)
}
