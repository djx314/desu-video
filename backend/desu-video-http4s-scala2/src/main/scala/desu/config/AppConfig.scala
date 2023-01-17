package desu.config

import desu.models
import desu.models._

import cats.effect._
import cats._
import cats.implicits._
import java.nio.file.{Path => JPath, Paths}

import doobie.hikari._

import scala.concurrent.ExecutionContext

class DesuConfigBuilder {
  import zio.config.magnolia.descriptor
  import zio.config.typesafe.TypesafeConfig
  import zio.{Runtime, Unsafe, ZIO}

  private val desuConfigAutomatic = descriptor[DesuConfig]
  private val layer               = TypesafeConfig.fromResourcePath(desuConfigAutomatic)
  private val desuConfigZIO       = ZIO.service[DesuConfig].provide(layer)

  def getModel[F[_]: Async]: F[DesuConfig] =
    Async[F].fromFuture(Sync[F].delay(Unsafe.unsafe(implicit unsafe => Runtime.default.unsafe.runToFuture(desuConfigZIO))))
}

object DesuConfigBuilder {
  def build: DesuConfigBuilder = new DesuConfigBuilder
}

class AppConfigBuilder(implicit config: DesuConfig) {
  import org.http4s.dsl.io.{Path, Root}
  import org.http4s.Uri.Path.Segment

  def getModel[F[_]: Sync]: F[AppConfig] = {
    val FilePageRoot: Path = Root / Segment("api") / Segment("desu")

    def rootPathImpl = Paths.get(config.desu.video.file.rootPath)
    val rootPathIO   = Sync[F].delay(rootPathImpl)

    for (rootPath <- rootPathIO) yield new AppConfig(rootPath = rootPath, FilePageRoot = FilePageRoot)
  }
}

class AppConfig(val rootPath: JPath, val FilePageRoot: org.http4s.dsl.io.Path)

object AppConfig {
  def build(implicit config: DesuConfig): AppConfigBuilder = new AppConfigBuilder
}

class DoobieDB(implicit config: DesuConfig) {
  import doobie._
  import doobie.implicits._

  def transactor[F[_]: Async]: Resource[F, HikariTransactor[F]] = {
    val dsConfig = config.mysqlDesuQuillDB.dataSource
    def fromExecContext(ce: ExecutionContext) = HikariTransactor.newHikariTransactor[F](
      driverClassName = dsConfig.driverClassName, // driver classname
      url = dsConfig.jdbcUrl,                     // connect URL
      user = dsConfig.username,                   // username
      pass = dsConfig.password,                   // password
      ce                                          // await connection here
    )
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](32)
      xa <- fromExecContext(ce)
    } yield xa
  }
}

object DoobieDB {
  def build(implicit config: DesuConfig): DoobieDB = new DoobieDB
}
