package gd.robot.akka.utils

import gd.robot.akka.gdactor.gohome.SkillsRoundAction2
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
case class JinengMatch(is1: Boolean, is2: Boolean)
case class SkillsImg(model: List[SkillsRoundAction2.Skill])

case class JavacvException(message: String, cause: Throwable) extends Exception(message, cause)

case class JinenglanImg(jineng1: Array[Byte], jineng2: Array[Byte], zhandou: Array[Byte])

class ImageMatcher(
  val compareInfo: List[CompareImg],
  val jinengImg: JinenglanImg,
  val skillsImg: SkillsImg,
  blockingContext: ExecutionContext
) {

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

  private def compare(compareImageByte: Array[Byte], screenshot: Array[Byte]) =
    Future(matchImg(compareImageByte = compareImageByte, screenshot))(blockingContext)

  def imgEnabled(implicit ec: ExecutionContext): Future[Boolean] = for {
    isZhandou <- screenshotF(x1 = 850, y1 = 850, x2 = 900, y2 = 900)
    result    <- compare(jinengImg.zhandou, isZhandou)
  } yield result

  def matchJineng(implicit ec: ExecutionContext): Future[JinengMatch] = {
    for {
      screenshot <- screenshotF(x1 = 580, y1 = 920, x2 = 680, y2 = 1000)
      result1    <- compare(jinengImg.jineng1, screenshot)
      result2    <- compare(jinengImg.jineng2, screenshot)
    } yield JinengMatch(is1 = result1, is2 = result2)
  }

  def matchDelay(implicit ec: ExecutionContext): Future[Option[SkillsRoundAction2.Skill]] = {
    def findFirst(screenshot: Array[Byte], list: List[SkillsRoundAction2.Skill]): Future[Option[SkillsRoundAction2.Skill]] = {
      list match {
        case head :: tail =>
          def compareNextImg(confirm: Boolean) = if (confirm) Future.successful(Option(head)) else findFirst(screenshot, tail)
          for {
            confirm <- compare(head.img, screenshot)
            result  <- compareNextImg(confirm)
          } yield result
        case Nil => Future.successful(Option.empty)
      }
    }
    for {
      screenshot <- screenshotF(x1 = 650, y1 = 940, x2 = 1300, y2 = 1000)
      result     <- findFirst(screenshot, skillsImg.model)
    } yield result
  }

  private def lantiaoPoint1(implicit ec: ExecutionContext): Future[Boolean] = {
    val img    = new WritableImage(1, 1)
    val action = SystemRobot.runJavafx(robot => robot.getScreenCapture(img, 1260, 915, 1, 1))
    for (_ <- action) yield {
      val color = img.getPixelReader.getColor(0, 0)
      val r     = color.getRed * 256
      val g     = color.getGreen * 256
      val b     = color.getBlue * 256
      r.toInt == 28 && g.toInt == 25 && b.toInt == 18
    }
  }

  private def lantiaoPoint2(implicit ec: ExecutionContext): Future[Boolean] = {
    val img    = new WritableImage(1, 1)
    val action = SystemRobot.runJavafx(robot => robot.getScreenCapture(img, 1320, 915, 1, 1))
    for (_ <- action) yield {
      val color = img.getPixelReader.getColor(0, 0)
      val r     = color.getRed * 256
      val g     = color.getGreen * 256
      val b     = color.getBlue * 256
      r.toInt == 22 && g.toInt == 17 && b.toInt == 12
    }
  }

  private def lantiaoPoint3(implicit ec: ExecutionContext): Future[Boolean] = {
    val img    = new WritableImage(1, 1)
    val action = SystemRobot.runJavafx(robot => robot.getScreenCapture(img, 1380, 915, 1, 1))
    for (_ <- action) yield {
      val color = img.getPixelReader.getColor(0, 0)
      val r     = color.getRed * 256
      val g     = color.getGreen * 256
      val b     = color.getBlue * 256
      r.toInt == 22 && g.toInt == 17 && b.toInt == 10
    }
  }

  def lantiaoPoint(implicit ec: ExecutionContext): Future[Int] = {
    def now1(is1: Boolean): Future[Int] = if (is1) Future.successful(1) else Future.successful(0)
    def now2(is2: Boolean): Future[Int] = if (is2) Future.successful(2)
    else
      for {
        is1 <- lantiaoPoint1
        r   <- now1(is1)
      } yield r
    def now3(is3: Boolean): Future[Int] = if (is3) Future.successful(3)
    else
      for {
        is2 <- lantiaoPoint2
        r   <- now2(is2)
      } yield r
    for {
      is3 <- lantiaoPoint3
      r   <- now3(is3)
    } yield r
  }

}

object ImageMatcher {
  def loadMat(arr: Array[Byte]): Mat = imdecode(new Mat(new BytePointer(arr: _*), false), IMREAD_ANYDEPTH | IMREAD_ANYCOLOR)
  def init(imgs: List[CompareImg], jinenglanImg: JinenglanImg, skillsImg: SkillsImg)(blockingec: ExecutionContext): ImageMatcher =
    new ImageMatcher(imgs, jinenglanImg, skillsImg, blockingec)
}
