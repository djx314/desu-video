package desu.video.test.model

case class RootPathFiles(files: List[String])

case class DirId(id: Long, fileName: String)

case class FileNotConfirmException(message: String) extends Exception(message)

case class RootFileNameRequest(fileName: String)
