package zdesu.endpoint

import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._
import sttp.tapir._
import sttp.tapir.json.jsoniter._
import sttp.tapir.generic.auto._
import zdesu.model.{DesuResult, DirId, RootFileNameRequest, RootPathFiles}

object HttpServerRoutingMinimal {

  val apiEndpoint = endpoint.in("api" / "desu")

  implicit val _stringNullable: JsonValueCodec[Option[String]] = JsonCodecMaker.make

  val rootPathFiles: PublicEndpoint[Unit, DesuResult[Option[String]], DesuResult[RootPathFiles], Any] =
    apiEndpoint.get.in("rootPathFiles").errorOut(jsonBody[DesuResult[Option[String]]]).out(jsonBody[DesuResult[RootPathFiles]])
  val rootPathFile: PublicEndpoint[RootFileNameRequest, DesuResult[Option[String]], DirId, Any] =
    apiEndpoint.post
      .in("rootPathFile")
      .in(jsonBody[RootFileNameRequest])
      .errorOut(jsonBody[DesuResult[Option[String]]])
      .out(jsonBody[DirId])

}
