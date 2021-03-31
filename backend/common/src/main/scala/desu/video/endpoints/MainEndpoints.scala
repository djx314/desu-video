package desu.video.endpoints

import desu.video.models.PathModel
import sttp.tapir._
import sttp.tapir.generic.auto._

object MainEndpoints {

  val fileList: Endpoint[(Long, PathModel), Unit, Unit, Any] = endpoint.post.in(query[Long]("pathId")).in(formBody[PathModel])

}
