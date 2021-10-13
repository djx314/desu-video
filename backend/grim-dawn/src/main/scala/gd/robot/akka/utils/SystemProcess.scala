package gd.robot.akka.utils

import akka.actor.typed.{ActorSystem, DispatcherSelector}
import gd.robot.akka.config.AppConfig

class SystemProcess(system: ActorSystem[Nothing], imageUtils: ImageUtils) {

  val blockExecutionContext     = system.dispatchers.lookup(DispatcherSelector.blocking())
  implicit val executionContext = system.dispatchers.lookup(AppConfig.gdSelector)

  val 开始菜单图标Byte     = ImageUtils.getBytesFromClasspath("/窗口定位/开始菜单图标.png")
  val 恐怖黎明拾落选择按钮Byte = ImageUtils.getBytesFromClasspath("/窗口定位/恐怖黎明拾落选择按钮.png")

}
