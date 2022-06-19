package desu.video.test.cases.endpoints

import sttp.tapir.PublicEndpoint
import sttp.tapir.ztapir.{query => _, *}
import sttp.client3.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.jsoniter.*
import desu.video.test.model.*

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker.*

object TestEndpoint:

  given jsonValueCodecImplicit1: JsonValueCodec[DesuResult[Option[String]]] = make
  given jsonValueCodecImplicit2: JsonValueCodec[DesuResult[RootPathFiles]]  = make

  private val apiPrefix = endpoint.in("api" / "desu")

  val rootPathFilesEndpoint: PublicEndpoint[Unit, DesuResult[Option[String]], DesuResult[RootPathFiles], Any] =
    apiPrefix.get.in("rootPathFiles").out(jsonBody[DesuResult[RootPathFiles]]).errorOut(jsonBody[DesuResult[Option[String]]])

  val rootPathFileEndpoint: PublicEndpoint[RootFileNameRequest, DesuResult[Option[String]], DirId, Any] =
    apiPrefix.post.in("rootPathFile").in(jsonBody[RootFileNameRequest]).out(jsonBody[DirId]).errorOut(jsonBody[DesuResult[Option[String]]])

end TestEndpoint
