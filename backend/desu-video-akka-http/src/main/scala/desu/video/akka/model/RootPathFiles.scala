package desu.video.akka.model

import zio.json.*

case class RootPathFiles(files: List[String])
object RootPathFiles {
  given JsonEncoder[RootPathFiles] = DeriveJsonEncoder.gen
  given JsonDecoder[RootPathFiles] = DeriveJsonDecoder.gen
}

case class DirId(id: Long, fileName: String)
object DirId {
  given JsonEncoder[DirId] = DeriveJsonEncoder.gen
  given JsonDecoder[DirId] = DeriveJsonDecoder.gen
}

case class FileNotConfirmException(message: String) extends Exception(message)

case class RootFileNameRequest(fileName: String)
object RootFileNameRequest {
  given JsonEncoder[RootFileNameRequest] = DeriveJsonEncoder.gen
  given JsonDecoder[RootFileNameRequest] = DeriveJsonDecoder.gen
}
