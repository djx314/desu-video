package zdesu.model

import julienrf.json.derived
import play.api.libs.json.OFormat

case class RootPathFiles(files: List[String])
object RootPathFiles {
  implicit val format: OFormat[RootPathFiles] = derived.oformat()
}

case class DirId(id: Long, fileName: String)
object DirId {
  implicit val format: OFormat[DirId] = derived.oformat()
}

case class FileNotConfirmException(message: String) extends Exception(message)

case class RootFileNameRequest(fileName: String)
object RootFileNameRequest {
  implicit val format: OFormat[RootFileNameRequest] = derived.oformat()
}
