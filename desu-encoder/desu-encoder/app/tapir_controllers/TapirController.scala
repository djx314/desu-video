package assist.controllers

import wiring.commons._
import zio._
import zio.console._

object TapirController {

  def isEncoding(uuid: String): ZIO[CurrentEncodeHas with Console, Nothing, String] =
    for {
      currentEncode <- currentEncode.zio
      i             <- ZIO.effectTotal(currentEncode.keyExists(uuid).toString)
      _             <- putStrLn(s"获取文件 uuid: ${uuid} 的返回值，是否存在: ${i}")
    } yield i

}
