package desu.video.common.endpoints

import sttp.tapir._
import sttp.tapir.client.sttp.SttpClientInterpreter

object MainEndpoints {

  val fileList: Endpoint[Long, Unit, Unit, Any] = endpoint.post.in("filePath" / "names").in(query[Long]("pathId"))
  def fileListUri(id: Long): String = {
    val req = SttpClientInterpreter.toRequestThrowDecodeFailures(fileList, Option.empty)
    req(id).uri.toString()
  }

}
