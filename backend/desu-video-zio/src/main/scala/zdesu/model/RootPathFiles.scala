package zdesu.model

import tethys._
import tethys.jackson._
import tethys.derivation.semiauto._

case class RootPathFiles(files: List[String])
object RootPathFiles {
  implicit val jWriter: JsonObjectWriter[RootPathFiles] = jsonWriter
  implicit val jReader: JsonReader[RootPathFiles]       = jsonReader
}

case class DirId(id: Long, fileName: String)
object DirId {
  implicit val jWriter: JsonObjectWriter[DirId] = jsonWriter
  implicit val jReader: JsonReader[DirId]       = jsonReader
}

case class FileNotConfirmException(message: String) extends Exception(message)

case class RootFileNameRequest(fileName: String)
object RootFileNameRequest {
  implicit val jWriter: JsonObjectWriter[RootFileNameRequest] = jsonWriter
  implicit val jReader: JsonReader[RootFileNameRequest]       = jsonReader
}
