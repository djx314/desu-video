package desu.config

import desu.models._
import cats.effect._
import cats._
import cats.effect.kernel.Resource
import cats.implicits._

import java.nio.file.{Path => JPath, Paths => JPaths}

import scala.concurrent.ExecutionContext

class DesuConfigBuilder {
  import zio.config.magnolia.descriptor
  import zio.config.typesafe.TypesafeConfig
  import zio.{Runtime, Unsafe, ZIO}

  private val desuConfigAutomatic = descriptor[DesuConfig]
  private val layer               = TypesafeConfig.fromResourcePath(desuConfigAutomatic)
  private val desuConfigZIO       = ZIO.service[DesuConfig].provide(layer)

  def getEffect[F[_]: Async]: F[DesuConfig] =
    Async[F].fromFuture(Sync[F].delay(Unsafe.unsafe(implicit unsafe => Runtime.default.unsafe.runToFuture(desuConfigZIO))))

  def getResource[F[_]: Async]: Resource[F, DesuConfig] = Resource.eval(getEffect)
}

object DesuConfigBuilder {
  def build: DesuConfigBuilder = new DesuConfigBuilder
}

class AppConfigBuilder(config: DesuConfig) {
  import org.http4s.dsl.io.{Path, Root}
  import org.http4s.Uri.Path.Segment

  val FilePageRoot: Path = Root / Segment("desu")

  def getEffect[F[_]: Sync]: F[AppConfig] = {
    def rootPathImpl = JPaths.get(config.desu.video.file.rootPath)
    val rootPathIO   = Sync[F].delay(rootPathImpl)

    for (rootPath <- rootPathIO) yield new AppConfig(rootPath = rootPath, FilePageRoot = FilePageRoot)
  }

  def getResource[F[_]: Sync]: Resource[F, AppConfig] = Resource.eval(getEffect)
}

class AppConfig(val rootPath: JPath, val FilePageRoot: org.http4s.dsl.io.Path)

object AppConfig {
  def build(implicit config: DesuConfig): AppConfigBuilder = new AppConfigBuilder(implicitly)
}

class DoobieDB(config: DesuConfig) {
  import doobie._
  import doobie.hikari._
  import doobie.implicits._

  private val dsConfig: DesuDataSource = config.mysqlDesuQuillDB.dataSource

  private def fromExecContext[F[_]: Async](ce: ExecutionContext): Resource[F, HikariTransactor[F]] =
    HikariTransactor.newHikariTransactor[F](
      driverClassName = dsConfig.driverClassName, // driver classname
      url = dsConfig.jdbcUrl,                     // connect URL
      user = dsConfig.username,                   // username
      pass = dsConfig.password,                   // password
      ce                                          // await connection here
    )

  def transactorResource[F[_]: Async]: Resource[F, Transactor[F]] = for {
    ce <- ExecutionContexts.fixedThreadPool[F](32)
    xa <- fromExecContext(ce)
  } yield xa
}

object DoobieDB {
  def build(implicit config: DesuConfig): DoobieDB = new DoobieDB(implicitly)
}
