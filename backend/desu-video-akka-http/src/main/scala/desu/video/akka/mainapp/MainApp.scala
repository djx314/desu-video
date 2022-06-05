package desu.video.akka.mainapp

import akka.Done
import akka.actor.typed.{ActorSystem, DispatcherSelector}
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
import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn
import io.getquill.util.LoadConfig

import javax.sql.DataSource
import io.getquill.*
import io.getquill.context.ZioJdbc.DataSourceLayer

object MainApp {

  given ActorSystem[Nothing]             = ActorSystem(Behaviors.empty, "my-system")
  private given (DataSource & Closeable) = JdbcContextConfig(LoadConfig("mysqlDesuDB")).dataSource

  private given AppConfig    = wire
  private given FileService  = wire
  private given FileFinder   = wire
  private given MysqlContext = wire

  given HttpServerRoutingMinimal = wire

  private val blockExecutionContext = implicitly[ActorSystem[Nothing]].dispatchers.lookup(DispatcherSelector.blocking())
  private given ExecutionContext    = implicitly[ActorSystem[Nothing]].dispatchers.lookup(implicitly[AppConfig].desuSelector)

  def closeProject: Future[Done] = {
    val close1 = Future(implicitly[DataSource & Closeable].close())(blockExecutionContext)
    for (_ <- close1) yield Done.done()
  }

}

object HttpServerRoutingMinimal {

  def main(args: Array[String]): Unit = {
    import MainApp.given
    val system: ActorSystem[Nothing]                       = implicitly
    val httpServerRoutingMinimal: HttpServerRoutingMinimal = implicitly

    // needed for the future flatMap/onComplete in the end
    given ExecutionContext = system.executionContext

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(httpServerRoutingMinimal.route)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    val shutdown1 = for {
      b <- bindingFuture
      _ <- b.unbind()
    } yield Done.done()
    val shutdown2 = MainApp.closeProject
    val shutdown = for {
      _ <- shutdown1
      _ <- shutdown2
    } yield Done.done()
    shutdown.onComplete(_ => system.terminate())
  }

}
