package zdesu.mainapp

import zio._
import zhttp.service.Server
import zdesu.fusion.Fusion

object MainApp extends ZIOAppDefault {

  case class AA(b: String, cc: Int, dd: Long)
  case class BB(ee: Char, ff: Short)
  case class CC(b: String, cc: Int, dd: Long, ee: Char, ff: Short)

  def fillCurrent[T](func: Any): T = {
    val data = func.asInstanceOf[Any => Any](null)
    if (data.isInstanceOf[Function[_, _]]) fillCurrent(data) else data.asInstanceOf[T]
  }

  val aa           = AA("miao", 2, 4)
  val bb           = BB('i', 2)
  val ccSample: CC = fillCurrent(CC.curried)
  println(CC)
  val ccName = ccSample.productElementNames.to(List)

  val aaMap                   = aa.productElementNames.to(List).zip(aa.productIterator.to(List)).to(Map)
  val bbMap                   = bb.productElementNames.to(List).zip(bb.productIterator.to(List)).to(Map)
  val ccMap: Map[String, Any] = aaMap ++ bbMap

  val input = (CC.apply _).curried

  def ccProName(n: Int): String = ccName(n)

  def fillMeUp[T](func: Any, data: Map[String, Any]): T = {
    var temp: Any = func
    for (i <- 0 to ccName.size - 1) yield {
      temp = temp.asInstanceOf[Any => Any](data(ccProName(i)))
    }
    temp.asInstanceOf[T]
  }

  val cc: CC = fillMeUp(input, ccMap)

  println(aa)
  println(bb)
  println(cc)

  override def run: URIO[ZIOAppArgs with Scope, ExitCode] =
    Server
      .start(8080, Fusion.http)
      .provideLayer(ProjectEnv.layer)
      .catchAllDefect(e => ZIO.logErrorCause("启动 http 服务发生错误", Cause.fail(e)))
      .exitCode

}
