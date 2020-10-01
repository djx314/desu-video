package models

import io.circe.{Decoder, Encoder}
import org.scalax.kirito.circe.KCirce

case class FilePath(
  fileName: String,
  isDirectory: Boolean,
  nginxUrl: String,
  tempUrl: String,
  requestUrl: String,
  temfileExists: Boolean,
  filePath: String,
  canEncode: Boolean,
  isEncoding: Boolean
)

object FilePath {
  object emptyTable
  implicit def filePathImplicitEncoder: Encoder[FilePath] = KCirce.encodeCaseClassWithTable(emptyTable)
  implicit def filePathImplicitDecoder: Decoder[FilePath] = KCirce.decodeCaseClassWithTable(emptyTable)
}

case class DirInfo(parentPath: String, urls: List[FilePath])

object DirInfo {
  object emptyTable
  implicit def filePathImplicitEncoder: Encoder[DirInfo] = KCirce.encodeCaseClassWithTable(emptyTable)
  implicit def filePathImplicitDecoder: Decoder[DirInfo] = KCirce.decodeCaseClassWithTable(emptyTable)
}

case class RequestInfo(isSuccessed: Boolean, message: String)

object RequestInfo {
  object emptyTable
  implicit def filePathImplicitEncoder: Encoder[RequestInfo] = KCirce.encodeCaseClassWithTable(emptyTable)
  implicit def filePathImplicitDecoder: Decoder[RequestInfo] = KCirce.decodeCaseClassWithTable(emptyTable)
}

case class VideoInfo(encodeType: String, videoKey: String, videoLength: Int, videoInfo: String, returnPath: String)

object VideoInfo {

  import play.api.data._
  import play.api.data.Forms._

  val videoForm = Form(
    mapping("encodeType" -> nonEmptyText, "videoKey" -> nonEmptyText, "videoLength" -> number, "videoInfo" -> nonEmptyText, "returnPath" -> nonEmptyText)(
      VideoInfo.apply
    )(VideoInfo.unapply)
  )
}

case class PathInfo(path: String)

object PathInfo {
  object emptyTable
  implicit def filePathImplicitEncoder: Encoder[PathInfo] = KCirce.encodeCaseClassWithTable(emptyTable)
  implicit def filePathImplicitDecoder: Decoder[PathInfo] = KCirce.decodeCaseClassWithTable(emptyTable)
}

case class PathAndHost(path: String, host: String)

object PathAndHost {
  object emptyTable
  implicit def filePathImplicitEncoder: Encoder[PathAndHost] = KCirce.encodeCaseClassWithTable(emptyTable)
  implicit def filePathImplicitDecoder: Decoder[PathAndHost] = KCirce.decodeCaseClassWithTable(emptyTable)
}

case class AssPathInfo(videoPath: String, assPath: String, assScale: BigDecimal)

object AssPathInfo {
  import play.api.data._
  import play.api.data.Forms._

  val assPathInfoForm = Form(mapping("videoPath" -> nonEmptyText, "assPath" -> nonEmptyText, "assScale" -> bigDecimal)(AssPathInfo.apply)(AssPathInfo.unapply))
}
