package desu.config

import desu.models
import desu.models.*
import language.experimental.fewerBraces

trait DesuConfigModel:
  import zio.config.magnolia.descriptor
  import zio.config.typesafe.TypesafeConfig
  import zio.{ZIO, given}
  import cats.effect.*
  import cats.*
  import zio.interop.catz.*
  import zio.interop.catz.implicits.given
  import zio.interop.cus.*

  private val desuConfigAutomatic = descriptor[DesuConfig]
  private val layer               = TypesafeConfig.fromResourcePath(desuConfigAutomatic)
  private val desuConfigZIO       = ZIO.service[DesuConfig].provide(layer)

  val configIO: IO[DesuConfig] = desuConfigZIO.toEffect
end DesuConfigModel

class DesuConfigModelImpl extends DesuConfigModel

trait AppConfig(config: DesuConfig):
  import cats.effect.IO
  import org.http4s.Uri.Path.Segment
  import org.http4s.dsl.io.{Path, Root}
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
  import cats.effect.cps.*

  val transactor: Resource[IO, HikariTransactor[IO]] = async[Resource[IO, *]] {
    val dsConfig = config.mysqlDesuQuillDB.dataSource
    val ce       = ExecutionContexts.fixedThreadPool[IO](32).await
    HikariTransactor
      .newHikariTransactor[IO](
        driverClassName = dsConfig.driverClassName, // driver classname
        url = dsConfig.jdbcUrl,                     // connect URL
        user = dsConfig.username,                   // username
        pass = dsConfig.password,                   // password
        ce                                          // await connection here
      )
      .await
  }

end DoobieDB

class DoobieDBImpl(using DesuConfig) extends DoobieDB(summon)
