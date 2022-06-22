package desu.config

import org.http4s.Uri.Path.Segment
import org.http4s.dsl.io.*

import java.nio.file.{Path as JPath, Paths}

import zio.config.magnolia.descriptor
import zio.config.typesafe.*
import zio.{IO as _, *}

import desu.models.*
import cats.effect.*

class DesuConfigModel:
  private def desuConfigAutomatic      = descriptor[DesuConfig]
  private def layer                    = TypesafeConfig.fromResourcePath(desuConfigAutomatic)
  private def desuConfigZIO            = ZIO.service[DesuConfig].provide(layer)
  private def configZIORUN: DesuConfig = Runtime.default.unsafeRunTask(desuConfigZIO)

  val configIO = IO.blocking(configZIORUN)
end DesuConfigModel

class AppConfig(desuConfigModel: DesuConfigModel):

  val FilePageRoot: Path = Root / Segment("api") / Segment("desu")

  val rootPath: IO[JPath] = for (config <- desuConfigModel.configIO) yield Paths.get(config.desu.video.file.rootPath)

end AppConfig
