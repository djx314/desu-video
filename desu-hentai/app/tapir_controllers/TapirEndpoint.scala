package tapir_controllers

import models.PathInfo
import sttp.model._
import sttp.tapir.Endpoint
import sttp.tapir.ztapir._
import sttp.tapir.json.circe._

object TapirEndpoint {
  val encodeEndpoint: Endpoint[String, Unit, Boolean, Any]      = endpoint.in("isEncoding").method(Method.GET).in(query[String]("uuid")).out(jsonBody[Boolean])
  val fileListEndpoint: Endpoint[Unit, Unit, List[String], Any] = endpoint.in("fileList").method(Method.GET).out(jsonBody[List[String]])

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

  val tempfile: Endpoint[List[String], String, String, Any] = endpoint
    .in("tempfile" / paths)
    .errorOut(statusCode(StatusCode.NotFound))
    .errorOut(plainBody[String].description("找不到的提示信息"))
    .out(statusCode(StatusCode.MovedPermanently).description("找到 url 的重定向信息"))
    .out(header[String]("Location").description("重定向 url"))
    .description("缓存文件查看路径，重定向到缓存文件路径，需要在 url 后补全路径")

  val picList: Endpoint[List[String], String, List[String], Any] =
    endpoint
      .in("picList1111" / paths)
      .errorOut(statusCode(StatusCode.NotFound))
      .errorOut(plainBody[String].description("找不到图片文件提示信息"))
      .out(jsonBody[List[String]].description("图片文件 Model"))
      .description("显示图片文件，需要在 url 后补全路径")

  import sttp.tapir.docs.openapi._
  import sttp.tapir.openapi._
  val docs =
    List(encodeEndpoint, encodeFile, fileListEndpoint, uploadEncodedFile, tempfile, picList).toOpenAPI(
      Info(title = "影音浏览", version = "1.0", description = Option("影音浏览 API"))
    )
}
