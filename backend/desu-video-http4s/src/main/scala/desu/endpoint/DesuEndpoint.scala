package desu.endpoint

import desu.config.AppConfig
import sttp.tapir._

object DesuEndpoint {

  val baiduPageEndpoint = AppConfig.filePageRoot.in("baiduPage").out(htmlBodyUtf8).errorOut(htmlBodyUtf8)

}
