package desu.video.akka.model

import io.circe.Codec

case class RootPathFiles(files: List[String]) derives Codec.AsObject

case class DirId(id: Long, fileName: String) derives Codec.AsObject

case class FileNotConfirmException(message: String) extends Exception(message)

case class RootFileNameRequest(fileName: String) derives Codec.AsObject
