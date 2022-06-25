package desu.video.test.cases.mainapp

import zio.config.*
import zio.config.magnolia.descriptor
import zio.config.typesafe.*
import sttp.model.*
import sttp.client3.*
import zio.*
import javax.sql.DataSource
import java.io.Closeable
import io.getquill.context.ZioJdbc.DataSourceLayer
import io.getquill.util.LoadConfig
import io.getquill.*
import sttp.client3.httpclient.zio.*

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
  val layer1: ULayer[ContextUri] = ZLayer.succeed(ContextUri(uri"http://127.0.0.1:8080"))
end ContextUri

object ContextJdbcDatabase:
  val layer: TaskLayer[DataSource] = DataSourceLayer.fromDataSource(JdbcContextConfig(LoadConfig("mysqlDesuQuillDB")).dataSource)
end ContextJdbcDatabase

object CommonLayer:
  val live: TaskLayer[ProjectEnv] = HttpClientZioBackend.layer() ++ DesuConfigModel.layer ++ ContextUri.layer1 ++ ContextJdbcDatabase.layer
end CommonLayer

type ProjectEnv = DataSource & SttpClient & DesuConfig & ContextUri

type DIO   = [Err, Data] =>> ZIO[ProjectEnv, Err, Data]
type DTask = [Data] =>> RIO[ProjectEnv, Data]
type DRIO  = [R, Data] =>> RIO[ProjectEnv & R, Data]

extension [R, E, A](obj: ZIO[R, E, A]) inline def provideD: ProvideSomePartiallyApplied[ProjectEnv, R, E, A] = obj.provideSome[ProjectEnv]
