package zdesu.mainapp

import zio.config._
import zio.config.magnolia.descriptor
import zio.config.typesafe._
import zio._

case class DesuConfig(desu: VideoConfig)
case class VideoConfig(video: FileConfig)
case class FileConfig(file: RootPath)
case class RootPath(rootPath: String)

object DesuConfigModel {
  private val desuConfigAutomatic = descriptor[DesuConfig]
  val layer                       = TypesafeConfig.fromResourcePath(desuConfigAutomatic)
}
