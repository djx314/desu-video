package tapir_controllers

import models.PathInfo
import sttp.model._
import sttp.tapir.Endpoint
import sttp.tapir.ztapir._
import sttp.tapir.json.circe._

object TapirEndpoint {
  val encodeEndpoint   = endpoint.in("isEncoding").method(Method.GET).in(query[String]("uuid")).out(jsonBody[Boolean])
  val fileListEndpoint = endpoint.in("fileList").method(Method.GET).out(jsonBody[List[String]])

  // 待实现 endpoint
  val encodeFile: Endpoint[PathInfo, Unit, (StatusCode, String), Any] = endpoint
    .in("encode")
    .method(Method.POST)
    .in(jsonBody[PathInfo].description("当前待转码文件路径"))
    .out(statusCode.and(plainBody[String].description("输出信息")))
    .description("发送转码指令")

  case class UploadFileForm(encodeType: String, videoKey: String, videoLength: Long, videoInfo: String, returnPath: String, video_0: Part[java.io.File])

  val uploadEncodedFile: Endpoint[UploadFileForm, Unit, String, Any] = endpoint
    .in("uploadEncodedFile")
    .method(Method.POST)
    .in(multipartBody[UploadFileForm].description("转码服务器返回的转码后的文件及其信息"))
    .out(plainBody[String].description("返回上传后信息"))
    .description("转码服务器返回转码文件")

  import sttp.tapir.docs.openapi._
  import sttp.tapir.openapi._
  val docs = List(encodeEndpoint, encodeFile, fileListEndpoint, uploadEncodedFile).toOpenAPI(Info(title = "影音浏览", version = "1.0", description = Option("影音浏览 API")))
}
