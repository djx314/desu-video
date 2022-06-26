package desu.video.akka.routes.test

import akka.actor.typed.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import desu.video.akka.config.AppConfig
import desu.video.akka.model.{DirId, RootFileNameRequest, RootPathFiles}
import desu.video.akka.routes.HttpServerRoutingMinimal
import org.scalatest.concurrent.ScalaFutures
import desu.video.common.model.DesuResult
import desu.video.common.quill.model.MysqlContext
import io.getquill.*
import desu.video.common.quill.model.desuVideo.dirMapping

import java.nio.file.Files

class FullTestKitExampleSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with ScalaFutures:
  given LoggingAdapter = Logging.getLogger(system, "akka-http-test")

  given ActorSystem[Nothing] = ActorSystem.wrap(system)

  val testWire = new TestWire
  import testWire.given
  val appConfig    = summon[AppConfig]
  val mysqlContext = summon[MysqlContext]

  import mysqlContext.{*, given}

  "rootPath should" should {

    "exists" in {
      Files.exists(appConfig.rootPath.futureValue) shouldBe true
    }

    "be a dirctory" in {
      Files.isDirectory(appConfig.rootPath.futureValue) shouldBe true
    }

  }

end FullTestKitExampleSpec
