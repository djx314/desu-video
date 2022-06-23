package desu.config

import desu.models.*

class DesuConfigModel:
  import zio.config.magnolia.descriptor
  import zio.config.typesafe.*
  import zio.{IO as _, *}
  import cats.effect.IO

  private def desuConfigAutomatic      = descriptor[DesuConfig]
  private def layer                    = TypesafeConfig.fromResourcePath(desuConfigAutomatic)
  private def desuConfigZIO            = ZIO.service[DesuConfig].provide(layer)
  private def configZIORUN: DesuConfig = Runtime.default.unsafeRunTask(desuConfigZIO)

  val configIO = IO.blocking(configZIORUN)
end DesuConfigModel

class AppConfig(using DesuConfig):
  import cats.effect.IO
  import org.http4s.Uri.Path.Segment
  import org.http4s.dsl.io.*
  import java.nio.file.{Path as JPath, Paths}

  private val config = summon[DesuConfig]

  val FilePageRoot: Path = Root / Segment("api") / Segment("desu")

  private def rootPathImpl = Paths.get(config.desu.video.file.rootPath)
  val rootPath: IO[JPath]  = IO(rootPathImpl)
end AppConfig

class DoobieDB(using DesuConfig):
  import doobie.*
  import doobie.implicits.given
  import doobie.hikari.*
  import cats.implicits.given
  import cats.effect.*

  private val config = summon[DesuConfig]

  private val dsConfigIO = IO(config.mysqlDesuQuillDB.dataSource)

  val transactor: Resource[IO, HikariTransactor[IO]] =
    for
      ce       <- ExecutionContexts.fixedThreadPool[IO](32) // our connect EC
      dsConfig <- Resource.eval(dsConfigIO)
      xa <- HikariTransactor.newHikariTransactor[IO](
        driverClassName = dsConfig.driverClassName, // driver classname
        url = dsConfig.jdbcUrl,                     // connect URL
        user = dsConfig.username,                   // username
        pass = dsConfig.password,                   // password
        ce                                          // await connection here
      )
    yield xa
end DoobieDB
