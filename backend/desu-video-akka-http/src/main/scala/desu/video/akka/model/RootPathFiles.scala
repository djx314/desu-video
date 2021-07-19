package desu.video.akka.model

import io.circe.generic.JsonCodec

@JsonCodec
case class RootPathFiles(dirConfirm: Boolean, files: List[String])

@JsonCodec
case class DirId(id: Long, fileName: String)

@JsonCodec
case class RootFileNameRequest(fileName: String)
