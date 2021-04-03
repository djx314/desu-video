package desu.video.tapir.endpoints

import desu.video.common.endpoints.MainEndpoints
import desu.video.common.models.PathModel
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.generic.auto._

object VideoEndpoints {

  val fileList: Endpoint[(Long, PathModel), String, List[String], Any] =
    MainEndpoints.fileList.in(formBody[PathModel]).out(jsonBody[List[String]]).errorOut(jsonBody[String])

  val docs: List[Endpoint[_, _, _, _]] = List(fileList)

}
