package gd.robot.akka.utils

import akka.actor.typed.{ActorSystem, DispatcherSelector}
import gd.robot.akka.config.AppConfig
import gd.robot.akka.gdactor.gohome.SkillsRoundAction2
import javafx.scene.input.KeyCode
import scala.concurrent.{ExecutionContext, Future}

case class CompareImg(img: Array[Byte], keyCode: KeyCode, delay: Long)
case class JinengMatch(is1: Boolean, is2: Boolean)
case class SkillsImg(model: List[SkillsRoundAction2.Skill])

case class JavacvException(message: String, cause: Throwable) extends Exception(message, cause)

case class JinenglanImg(jineng1: Array[Byte], jineng2: Array[Byte], zhandou: Array[Byte])

case class ImageMatcherEnv(compareInfo: List[CompareImg], jinengImg: JinenglanImg, skillsImg: SkillsImg)

class ImageMatcher(
  env: ImageMatcherEnv,
  system: ActorSystem[Nothing],
  imageUtils: ImageUtils
) {
  val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)

  def matchImgs(implicit ec: ExecutionContext): Future[List[CompareImg]] = {
    def compare(screenshot: Array[Byte]) = env.compareInfo.filter(s => ImageUtils.matchImg(compareImageByte = s.img, screenshot).isEmpty)
    for {
      screenshot <- imageUtils.screenshotF(x1 = 500, y1 = 800, x2 = 900, y2 = 900)
      result     <- Future(compare(screenshot))(blockExecutionContext)
    } yield result
  }

  private def compare(compareImageByte: Array[Byte], screenshot: Array[Byte]) =
    Future(ImageUtils.matchImg(compareImageByte = compareImageByte, screenshot))(blockExecutionContext)

  def imgEnabled(implicit ec: ExecutionContext): Future[Boolean] = for {
    isZhandou <- imageUtils.screenshotF(x1 = 850, y1 = 850, x2 = 900, y2 = 900)
    result    <- compare(env.jinengImg.zhandou, isZhandou)
  } yield result.isDefined

  def matchJineng(implicit ec: ExecutionContext): Future[JinengMatch] = {
    for {
      screenshot <- imageUtils.screenshotF(x1 = 580, y1 = 920, x2 = 680, y2 = 1000)
      result1    <- compare(env.jinengImg.jineng1, screenshot)
      result2    <- compare(env.jinengImg.jineng2, screenshot)
    } yield JinengMatch(is1 = result1.isDefined, is2 = result2.isDefined)
  }

  def matchDelay(implicit ec: ExecutionContext): Future[Option[SkillsRoundAction2.Skill]] = {
    def findFirst(screenshot: Array[Byte], list: List[SkillsRoundAction2.Skill]): Future[Option[SkillsRoundAction2.Skill]] = {
      list match {
        case head :: tail =>
          def compareNextImg(confirm: Boolean) = if (confirm) Future.successful(Option(head)) else findFirst(screenshot, tail)
          for {
            confirm <- compare(head.img, screenshot)
            result  <- compareNextImg(confirm.isDefined)
          } yield result
        case Nil => Future.successful(Option.empty)
      }
    }
    for {
      screenshot <- imageUtils.screenshotF(x1 = 650, y1 = 940, x2 = 1300, y2 = 1000)
      result     <- findFirst(screenshot, env.skillsImg.model)
    } yield result
  }

  private def lantiaoPoint1(implicit ec: ExecutionContext): Future[Boolean] =
    for (color <- imageUtils.getColor(x = 1260, y = 915)) yield color.red == 28 && color.green == 25 && color.blue == 18

  private def lantiaoPoint2(implicit ec: ExecutionContext): Future[Boolean] =
    for (color <- imageUtils.getColor(x = 1320, y = 915)) yield color.red == 22 && color.green == 17 && color.blue == 12

  private def lantiaoPoint3(implicit ec: ExecutionContext): Future[Boolean] =
    for (color <- imageUtils.getColor(x = 1380, y = 915)) yield color.red == 22 && color.green == 17 && color.blue == 10

  def lantiaoPoint(implicit ec: ExecutionContext): Future[Int] = {
    def action = for {
      is3 <- lantiaoPoint3
      r   <- if (is3) Future.successful(3) else now2
    } yield r

    def now2: Future[Int] =
      for {
        is2 <- lantiaoPoint2
        r   <- if (is2) Future.successful(2) else now1
      } yield r

    def now1: Future[Int] = for (is1 <- lantiaoPoint1) yield if (is1) 1 else 0

    action

  }

}
