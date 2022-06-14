package desu.video.test.cases.endpoints

import sttp.tapir.PublicEndpoint
import sttp.tapir.ztapir.{query => _, *}
import sttp.client3.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.zio.*

import desu.video.test.model.*

object TestEndpoint:

  private val apiPrefix = endpoint.in("api" / "desu")

  val rootPathFilesEndpoint: PublicEndpoint[Unit, DesuResult[Option[String]], DesuResult[RootPathFiles], Any] =
    apiPrefix.get.in("rootPathFiles").out(jsonBody[DesuResult[RootPathFiles]]).errorOut(jsonBody[DesuResult[Option[String]]])

  val rootPathFileEndpoint: PublicEndpoint[RootFileNameRequest, DesuResult[Option[String]], DirId, Any] =
    apiPrefix.post.in("rootPathFile").in(jsonBody[RootFileNameRequest]).out(jsonBody[DirId]).errorOut(jsonBody[DesuResult[Option[String]]])

end TestEndpoint
