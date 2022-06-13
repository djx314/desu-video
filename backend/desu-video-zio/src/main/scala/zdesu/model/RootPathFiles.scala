package zdesu.model

import io.circe.generic.JsonCodec

@JsonCodec
case class RootPathFiles(files: List[String])

@JsonCodec
case class DirId(id: Long, fileName: String)

case class FileNotConfirmException(message: String) extends Exception(message)

@JsonCodec
case class RootFileNameRequest(fileName: String)
