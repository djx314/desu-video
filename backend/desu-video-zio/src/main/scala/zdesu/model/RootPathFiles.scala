package zdesu.model

import com.github.plokhotnyuk.jsoniter_scala.macros._
import com.github.plokhotnyuk.jsoniter_scala.core._

case class RootPathFiles(files: List[String])
object RootPathFiles {
  implicit def codec: JsonValueCodec[RootPathFiles] = JsonCodecMaker.make
}

case class DirId(id: Long, fileName: String)
object DirId {
  implicit def codec: JsonValueCodec[DirId] = JsonCodecMaker.make
}

case class FileNotConfirmException(message: String) extends Exception(message)

case class RootFileNameRequest(fileName: String)
object RootFileNameRequest {
  implicit def codec: JsonValueCodec[RootFileNameRequest] = JsonCodecMaker.make
}
