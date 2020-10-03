package tapir_controllers

import zio._
import zio.console._

object TapirHandle {

  def isEncoding(uuid: String): ZIO[Console, Nothing, Boolean] =
    for {
      i <- ZIO.succeed(true)
      _ <- putStrLn(s"获取文件 uuid: ${uuid} 的返回值，是否存在: ${i}")
    } yield i

  val fileListI = List("一个 glavo", "两个 glavm", "三个 呜喵王")
  def fileList(n: Unit): ZIO[Console, Nothing, List[String]] =
    for {
      i <- ZIO.succeed(fileListI)
      _ <- putStrLn(s"获取目录文件")
    } yield i

}
