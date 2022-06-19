package desu.video.test.model

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker.*

case class RootPathFiles(files: List[String])
object RootPathFiles {
  given bb: JsonValueCodec[RootPathFiles] = make
}

case class DirId(id: Long, fileName: String)
object DirId {
  given JsonValueCodec[DirId] = make
}

case class FileNotConfirmException(message: String) extends Exception(message)

case class RootFileNameRequest(fileName: String)
object RootFileNameRequest {
  given JsonValueCodec[RootFileNameRequest] = make
}
