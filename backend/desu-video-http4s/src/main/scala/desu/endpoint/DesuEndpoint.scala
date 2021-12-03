package desu.endpoint

import desu.config.AppConfig
import desu.models.{DirInfo, ResultSet}
import io.circe.generic.JsonCodec
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.generic.auto._

object DesuEndpoint {

  @JsonCodec
  case class InputFileName(fileName: String)

  val baiduPageEndpoint = AppConfig.filePageRoot.in("baiduPage").out(htmlBodyUtf8).errorOut(htmlBodyUtf8)
  val rootPathFileEndpoint = AppConfig.filePageRoot.post
    .in("rootPathFile")
    .in(jsonBody[InputFileName])
    .out(jsonBody[ResultSet[Option[DirInfo]]])
    .errorOut(jsonBody[ResultSet[String]])
  val rootPathFilesEndpoint =
    AppConfig.filePageRoot.in("rootPathFiles").out(jsonBody[ResultSet[Option[DirInfo]]]).errorOut(jsonBody[ResultSet[String]])

}
