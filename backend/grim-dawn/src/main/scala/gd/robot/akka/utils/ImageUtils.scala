package gd.robot.akka.utils

import akka.actor.typed.{ActorSystem, DispatcherSelector}
import gd.robot.akka.config.AppConfig
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
import scala.concurrent.Future
import scala.util.Using

case class ColorRGB(red: Int, green: Int, blue: Int)

class ImageUtils(system: ActorSystem[Nothing]) {
  val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)

  def screenshotF(x1: Int, y1: Int, x2: Int, y2: Int): Future[Array[Byte]] = {
    val img = new WritableImage(x2 - x1, y2 - y1)
    val f   = SystemRobot.runJavafx(robot => robot.getScreenCapture(img, x1, y1, x2 - x1, y2 - y1))
    def write = Future(
      Using.resource(new ByteArrayOutputStream) { io =>
        ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", io)
        io.toByteArray
      }
    )(blockExecutionContext)
    for {
      _   <- f
      img <- write
    } yield img
  }

  def getColor(x: Int, y: Int): Future[ColorRGB] = {
    val img    = new WritableImage(1, 1)
    val action = SystemRobot.runJavafx(robot => robot.getScreenCapture(img, x, y, 1, 1))
    for (_ <- action) yield {
      val color = img.getPixelReader.getColor(0, 0)
      val r     = color.getRed * 256
      val g     = color.getGreen * 256
      val b     = color.getBlue * 256
      ColorRGB(r.toInt, g.toInt, b.toInt)
    }
  }
}

object ImageUtils {

  case class JavacvException(message: String, cause: Throwable) extends Exception(message, cause)

  def getBytesFromClasspath(path: String): Array[Byte] = Using.resource(getClass.getResourceAsStream(path))(_.readAllBytes())
  def loadMat(arr: Array[Byte]): Mat = imdecode(new Mat(new BytePointer(arr: _*), false), IMREAD_ANYDEPTH | IMREAD_ANYCOLOR)

  def matchImg(compareImageByte: Array[Byte], screenshotByte: Array[Byte]): Option[(Int, Int)] = try {
    Using.resources(cvIplImage(loadMat(screenshotByte)), cvIplImage(loadMat(compareImageByte))) { (source, compareImage) =>
      def i =
        cvCreateImage(cvSize(source.width() - compareImage.width() + 1, source.height() - compareImage.height() + 1), IPL_DEPTH_32F, 1)
      Using.resource(i) { result =>
        cvZero(result)
        cvMatchTemplate(source, compareImage, result, CV_TM_CCORR_NORMED)
        def loc        = new CvPoint()
        def valPointer = new DoublePointer(0d, 0d)
        Using.resources(loc, loc, valPointer, valPointer) { (minLoc, maxLoc, minVal, maxVal) =>
          cvMinMaxLoc(result, minVal, maxVal, minLoc, maxLoc, null)
          if (maxVal.get() > 0.99f) Some(minLoc.x(), minLoc.y()) else Option.empty
        }
      }(cvReleaseImage)
    }
  } catch {
    case t: Throwable => throw JavacvException("Error throws when compare image.", t)
  }

}
