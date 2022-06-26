package desu.video.akka.routes.test

import akka.actor.typed.ActorSystem
import com.softwaremill.macwire.*
import desu.video.akka.config.AppConfig
import desu.video.akka.routes.HttpServerRoutingMinimal
import desu.video.akka.service.{FileFinder, FileService}
import desu.video.common.quill.model.MysqlContext
import scala.concurrent.ExecutionContext
import io.getquill.util.LoadConfig
import javax.sql.DataSource
import java.io.Closeable
import io.getquill.*

class TestWire(using ActorSystem[Nothing]):

  given AppConfig = wire

  private given FileService = wire
  private given FileFinder  = wire

  given HttpServerRoutingMinimal = wire

  given MysqlContext                     = wire
  private given (DataSource & Closeable) = JdbcContextConfig(LoadConfig("mysqlDesuQuillDB")).dataSource

end TestWire
