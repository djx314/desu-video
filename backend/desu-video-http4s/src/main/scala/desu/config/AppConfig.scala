package desu.config

import desu.models.*

trait DesuConfigModel:
  import zio.config.magnolia.descriptor
  import zio.config.typesafe.*
  import zio.{IO as _, *}
  import cats.effect.IO
  import scala.concurrent.Future

  private def desuConfigAutomatic              = descriptor[DesuConfig]
  private def layer                            = TypesafeConfig.fromResourcePath(desuConfigAutomatic)
  private def desuConfigZIO                    = ZIO.service[DesuConfig].provide(layer)
  private def configZIORUN: Future[DesuConfig] = Runtime.default.unsafeRunToFuture(desuConfigZIO)

  val configIO: IO[DesuConfig] = IO.fromFuture(IO.delay(configZIORUN))
end DesuConfigModel

class DesuConfigModelImpl extends DesuConfigModel

trait AppConfig(config: DesuConfig):
  import cats.effect.IO
  import org.http4s.Uri.Path.Segment
  import org.http4s.dsl.io.*
  import java.nio.file.{Path as JPath, Paths}

  val FilePageRoot: Path = Root / Segment("api") / Segment("desu")

  private def rootPathImpl = Paths.get(config.desu.video.file.rootPath)
  val rootPath: IO[JPath]  = IO(rootPathImpl)
end AppConfig

class AppConfigImpl(using DesuConfig) extends AppConfig(summon)

trait DoobieDB(config: DesuConfig):
  import doobie.*
  import doobie.implicits.given
  import doobie.hikari.*
  import cats.implicits.given
  import cats.effect.*

  private val dsConfigIO = IO(config.mysqlDesuQuillDB.dataSource)

  val transactor: Resource[IO, HikariTransactor[IO]] = for
    dsConfig <- Resource.eval(dsConfigIO)
    ce       <- ExecutionContexts.fixedThreadPool[IO](32) // our connect EC
    xa <- HikariTransactor.newHikariTransactor[IO](
      driverClassName = dsConfig.driverClassName, // driver classname
      url = dsConfig.jdbcUrl,                     // connect URL
      user = dsConfig.username,                   // username
      pass = dsConfig.password,                   // password
      ce                                          // await connection here
    )
  yield xa

end DoobieDB

class DoobieDBImpl(using DesuConfig) extends DoobieDB(summon)
