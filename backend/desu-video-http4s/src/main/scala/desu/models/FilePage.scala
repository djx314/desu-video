package desu.models

import desu.video.common.quill.model.desuVideo._
import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.circe.generic.JsonCodec

object CirceImplicit {
  implicit val dirMappingCodec: Codec[dirMapping] = deriveCodec
}

@JsonCodec
case class ResultSet[T](data: T)
@JsonCodec
case class RootFilePaths(files: List[String])
@JsonCodec
case class DirInfo(dirInfo: dirMapping, subFiles: List[String], isDir: Boolean)
object DirInfo {
  import CirceImplicit._
}
