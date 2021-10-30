package gd.robot.akka.utils

import akka.actor.typed.{ActorSystem, DispatcherSelector}
import gd.robot.akka.config.AppConfig
import gd.robot.akka.gdactor.gohome.SkillsRoundAction2
import javafx.scene.input.KeyCode
import scala.concurrent.Future

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
  private val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  private implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)

  def matchImgs: Future[List[CompareImg]] = {
    def compare(screenshot: Array[Byte]) = {
      def compareImg(toCompare: Array[Byte]) = imageUtils.matchImg(compareImageByte = toCompare, screenshot)
      def tranImg(s: CompareImg)             = for (confirm <- compareImg(s.img)) yield if (confirm.isEmpty) Some(s) else Option.empty
      val pipei                              = env.compareInfo.map(tranImg)
      for (list <- Future.sequence(pipei)) yield list.collect { case Some(s) => s }
    }
    for {
      screenshot <- imageUtils.screenshotF(x1 = 500, y1 = 800, x2 = 900, y2 = 900)
      result     <- compare(screenshot)
    } yield result
  }

  def imgEnabled: Future[Boolean] = for {
    isZhandou <- imageUtils.screenshotF(x1 = 850, y1 = 850, x2 = 900, y2 = 900)
    result    <- imageUtils.matchImg(env.jinengImg.zhandou, isZhandou)
  } yield result.isDefined

  def matchJineng: Future[JinengMatch] = for {
    screenshot <- imageUtils.screenshotF(x1 = 580, y1 = 920, x2 = 680, y2 = 1000)
    result1    <- imageUtils.matchImg(env.jinengImg.jineng1, screenshot)
    result2    <- imageUtils.matchImg(env.jinengImg.jineng2, screenshot)
  } yield JinengMatch(is1 = result1.isDefined, is2 = result2.isDefined)

  // 从屏幕中找到第一个匹配可以输出的技能
  def matchDelay: Future[Option[SkillsRoundAction2.Skill]] = {
    def findFirst(screenshot: Array[Byte], list: List[SkillsRoundAction2.Skill]): Future[Option[SkillsRoundAction2.Skill]] = {
      list match {
        case head :: tail =>
          def compareNextImg(confirm: Boolean) = if (confirm) Future.successful(Option(head)) else findFirst(screenshot, tail)
          for {
            // 在屏幕截图中寻找合法的技能截图（冷却时间为 0）
            confirm <- imageUtils.matchImg(head.img, screenshot)
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

  private def lantiaoPoint1: Future[Boolean] =
    for (color <- imageUtils.getColor(x = 1260, y = 915)) yield color.red == 28 && color.green == 25 && color.blue == 18

  private def lantiaoPoint2: Future[Boolean] =
    for (color <- imageUtils.getColor(x = 1320, y = 915)) yield color.red == 22 && color.green == 17 && color.blue == 12

  private def lantiaoPoint3: Future[Boolean] =
    for (color <- imageUtils.getColor(x = 1380, y = 915)) yield color.red == 22 && color.green == 17 && color.blue == 10

  def lantiaoPoint: Future[Int] = {
    def action = for {
      is3 <- lantiaoPoint3
      r   <- if (is3) Future.successful(3) else now2
    } yield r

    def now2: Future[Int] = for {
      is2 <- lantiaoPoint2
      r   <- if (is2) Future.successful(2) else now1
    } yield r

    def now1: Future[Int] = for (is1 <- lantiaoPoint1) yield if (is1) 1 else 0

    action
  }

}
