package desu.video.test.cases

import zio.config.*
import zio.config.magnolia.descriptor
import zio.config.typesafe.*
import sttp.model.*
import sttp.client3.*
import zio.*

case class DesuConfig(desu: VideoConfig)
case class VideoConfig(video: FileConfig)
case class FileConfig(file: RootPath)
case class RootPath(rootPath: String)

object DesuConfigModel:

  private val desuConfigAutomatic = descriptor[DesuConfig]
  val layer                       = TypesafeConfig.fromResourcePath(desuConfigAutomatic)

end DesuConfigModel

class ContextUri(val uri: Uri)

object ContextUri:

  val live1: ULayer[ContextUri] = ZLayer.succeed(ContextUri(uri"http://127.0.0.1:8080"))

end ContextUri
