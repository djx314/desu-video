package gd.robot.akka.config

import akka.actor.typed.{ActorSystem, DispatcherSelector}
import gd.robot.akka.gdactor.gohome.{ActionQueue, SkillsRoundAction2}
import gd.robot.akka.utils.{CompareImg, ImageMatcher, ImageMatcherEnv, ImageUtils, JinenglanImg, SkillsImg}
import javafx.scene.input.KeyCode

class AppConfig(system: ActorSystem[Nothing]) {
  val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)

  val 责难光环Byte     = ImageUtils.getBytesFromClasspath("/责难光环.png")
  val 附身烈焰Byte     = ImageUtils.getBytesFromClasspath("/附身烈焰.png")
  val 复仇烈焰Byte     = ImageUtils.getBytesFromClasspath("/复仇烈焰.png")
  val 阿兹拉格瑞安战术Byte = ImageUtils.getBytesFromClasspath("/阿兹拉格瑞安战术.png")
  val 旋转刀刃Byte     = ImageUtils.getBytesFromClasspath("/旋转刀刃.png")

  val 德里格之创Byte  = ImageUtils.getBytesFromClasspath("/德里格之创.png")
  val 手榴弹Byte    = ImageUtils.getBytesFromClasspath("/手榴弹.png")
  val 强化盾牌猛击Byte = ImageUtils.getBytesFromClasspath("/强化盾牌猛击.png")
  val 粉碎大地Byte   = ImageUtils.getBytesFromClasspath("/粉碎大地.png")

  val 技能栏1Byte   = ImageUtils.getBytesFromClasspath("/技能栏1.png")
  val 技能栏2Byte   = ImageUtils.getBytesFromClasspath("/技能栏2.png")
  val 是否战斗状态Byte = ImageUtils.getBytesFromClasspath("/是否战斗状态.png")

  val listImg: List[CompareImg] = List(
    CompareImg(责难光环Byte, KeyCode.DIGIT6, 100),
    CompareImg(附身烈焰Byte, KeyCode.DIGIT7, 500),
    CompareImg(复仇烈焰Byte, KeyCode.DIGIT8, 100),
    CompareImg(阿兹拉格瑞安战术Byte, KeyCode.DIGIT9, 500),
    CompareImg(旋转刀刃Byte, KeyCode.DIGIT0, 0)
  )

  val jinenglanImg = JinenglanImg(jineng1 = 技能栏1Byte, jineng2 = 技能栏2Byte, zhandou = 是否战斗状态Byte)

  val 德里格之创  = SkillsRoundAction2.Skill(德里格之创Byte, List(ActionQueue.MouseRightClick))
  val 手榴弹    = SkillsRoundAction2.Skill(手榴弹Byte, List(ActionQueue.KeyType(KeyCode.DIGIT1)))
  val 强化盾牌猛击 = SkillsRoundAction2.Skill(强化盾牌猛击Byte, List(ActionQueue.MouseClick))
  val 粉碎大地   = SkillsRoundAction2.Skill(粉碎大地Byte, List(ActionQueue.KeyType(KeyCode.DIGIT8)))

  val skillsImg = SkillsImg(List(德里格之创, 手榴弹, 强化盾牌猛击))

  val imgMatch = ImageMatcherEnv(listImg, jinenglanImg, skillsImg)
}

object AppConfig {
  val defaultDispatcherName = "desu-dispatcher"
  val gdSelector            = DispatcherSelector.fromConfig(defaultDispatcherName)
}
