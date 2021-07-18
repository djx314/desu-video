package models

import io.circe.{Decoder, Encoder}
import ugeneric.circe.UCirce

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
  implicit def filePathImplicitEncoder: Encoder[FilePath] = UCirce.encodeCaseClass
  implicit def filePathImplicitDecoder: Decoder[FilePath] = UCirce.decodeCaseClass
}

case class DirInfo(parentPath: String, urls: List[FilePath])

object DirInfo {
  implicit def filePathImplicitEncoder: Encoder[DirInfo] = UCirce.encodeCaseClass
  implicit def filePathImplicitDecoder: Decoder[DirInfo] = UCirce.decodeCaseClass
}

case class RequestInfo(isSuccessed: Boolean, message: String)

object RequestInfo {
  implicit def filePathImplicitEncoder: Encoder[RequestInfo] = UCirce.encodeCaseClass
  implicit def filePathImplicitDecoder: Decoder[RequestInfo] = UCirce.decodeCaseClass
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
  implicit def filePathImplicitEncoder: Encoder[PathInfo] = UCirce.encodeCaseClass
  implicit def filePathImplicitDecoder: Decoder[PathInfo] = UCirce.decodeCaseClass
}

case class PathAndHost(path: String, host: String)

object PathAndHost {
  implicit def filePathImplicitEncoder: Encoder[PathAndHost] = UCirce.encodeCaseClass
  implicit def filePathImplicitDecoder: Decoder[PathAndHost] = UCirce.decodeCaseClass
}

case class AssPathInfo(videoPath: String, assPath: String, assScale: BigDecimal)

object AssPathInfo {
  import play.api.data._
  import play.api.data.Forms._

  val assPathInfoForm = Form(mapping("videoPath" -> nonEmptyText, "assPath" -> nonEmptyText, "assScale" -> bigDecimal)(AssPathInfo.apply)(AssPathInfo.unapply))
}
