package desu.models

import io.circe.Codec

case class RootPathFiles(files: List[String]) derives Codec.AsObject

case class DirId(id: Long, fileName: String) derives Codec.AsObject

case class FileNotConfirmException(message: String) extends Exception(message)

case class RootFileNameRequest(fileName: String) derives Codec.AsObject

case class DesuConfig(desu: VideoConfig)
case class VideoConfig(video: FileConfig)
case class FileConfig(file: RootPath)
case class RootPath(rootPath: String)
