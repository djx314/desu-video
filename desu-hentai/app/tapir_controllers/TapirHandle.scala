package tapir_controllers

import zio._
import zio.console._

object TapirHandle {

  def isEncoding(uuid: String): ZIO[Console, Nothing, Boolean] =
    for {
      i <- ZIO.succeed(true)
      _ <- putStrLn(s"获取文件 uuid: ${uuid} 的返回值，是否存在: ${i}")
    } yield i

}
