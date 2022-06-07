package desu.video.test.cases

import zio.config._
import zio.config.magnolia.descriptor
import zio.config.typesafe._

case class DesuConfig(desu: VideoConfig)
case class VideoConfig(video: FileConfig)
case class FileConfig(file: RootPath)
case class RootPath(rootPath: String)

object DesuConfigModel:

  val desuConfigAutomatic = descriptor[DesuConfig]
  val layer               = TypesafeConfig.fromResourcePath(desuConfigAutomatic)

end DesuConfigModel
