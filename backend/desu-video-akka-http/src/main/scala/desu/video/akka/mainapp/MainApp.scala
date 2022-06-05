package desu.video.akka.mainapp

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import com.softwaremill.macwire.*
import desu.video.akka.config.AppConfig
import desu.video.akka.service.FileService
import akka.http.scaladsl.Http
import desu.video.akka.routes.HttpServerRoutingMinimal
import desu.video.akka.service.FileFinder
import desu.video.common.quill.model.MysqlContext

import java.io.Closeable
import javax.sql.DataSource
import scala.concurrent.ExecutionContext
import scala.io.StdIn
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

object MainApp {

  private def buildConfig = {
    val hikariConfig = new HikariConfig()
    // 基础配置
    hikariConfig.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/desu_video")
    hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver")
    hikariConfig.setUsername("root")
    hikariConfig.setPassword("root")
    // 连接池配置
    hikariConfig.setPoolName("dev-hikari-pool")
    hikariConfig.setMinimumIdle(4)
    hikariConfig.setMaximumPoolSize(8)
    hikariConfig.setIdleTimeout(600000L)
    hikariConfig
  }

  given ActorSystem[Nothing]                = ActorSystem(Behaviors.empty, "my-system")
  private given (DataSource with Closeable) = new HikariDataSource(buildConfig)

  private given AppConfig    = wire
  private given FileService  = wire
  private given FileFinder   = wire
  private given MysqlContext = wire

  given HttpServerRoutingMinimal = wire

}

object HttpServerRoutingMinimal {

  def main(args: Array[String]): Unit = {
    import MainApp.given
    val system                   = implicitly[ActorSystem[Nothing]]
    val httpServerRoutingMinimal = implicitly[HttpServerRoutingMinimal]
    // needed for the future flatMap/onComplete in the end
    given ExecutionContext = system.executionContext

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(httpServerRoutingMinimal.route)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind())                 // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

}
