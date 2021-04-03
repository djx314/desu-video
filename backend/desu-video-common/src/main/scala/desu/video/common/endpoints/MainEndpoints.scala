package desu.video.common.endpoints

import sttp.tapir._
import sttp.tapir.client.sttp.SttpClientInterpreter

object MainEndpoints {

  val fileList: Endpoint[Long, Unit, Unit, Any] =
    endpoint.post.in("filePath" / "names").in(query[Long]("pathId")).description("获取本文件夹下文件名称").tag("前台页面响应")
  def fileListUri(id: Long): String = {
    val req = SttpClientInterpreter.toRequestThrowDecodeFailures(fileList, Option.empty)
    req(id).uri.toString()
  }

}
