package gd.robot.akka.config

import akka.actor.typed.{ActorSystem, DispatcherSelector}
import gd.robot.akka.utils.{CompareImg, ImageMatcher}
import javafx.scene.input.KeyCode

import scala.util.Using

class AppConfig(system: ActorSystem[Nothing]) {
  val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)

  private def getBytesClasspath(path: String): Array[Byte] = Using.resource(getClass.getResourceAsStream(path))(_.readAllBytes())

  val 责难光环Byte     = getBytesClasspath("/责难光环.png")
  val 附身烈焰Byte     = getBytesClasspath("/附身烈焰.png")
  val 复仇烈焰Byte     = getBytesClasspath("/复仇烈焰.png")
  val 阿兹拉格瑞安战术Byte = getBytesClasspath("/阿兹拉格瑞安战术.png")
  val 旋转刀刃Byte     = getBytesClasspath("/旋转刀刃.png")

  val 技能栏2Byte = getBytesClasspath("/技能栏2.png")

  val listImg: List[CompareImg] = List(
    CompareImg(责难光环Byte, KeyCode.DIGIT1, 100),
    CompareImg(附身烈焰Byte, KeyCode.DIGIT2, 500),
    CompareImg(复仇烈焰Byte, KeyCode.DIGIT3, 100),
    CompareImg(阿兹拉格瑞安战术Byte, KeyCode.DIGIT4, 500),
    CompareImg(旋转刀刃Byte, KeyCode.DIGIT5, 0)
  )

  val imgMatch = ImageMatcher.init(listImg, 技能栏2Byte)(blockingec = blockExecutionContext)
}

object AppConfig {
  val defaultDispatcherName = "desu-dispatcher"
  val gdSelector            = DispatcherSelector.fromConfig(defaultDispatcherName)
}
