package desu.video.akka.routes.test

import akka.actor.typed.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport.*
import desu.video.akka.config.AppConfig
import desu.video.akka.model.{DirId, RootFileNameRequest, RootPathFiles}
import desu.video.akka.routes.HttpServerRoutingMinimal
import org.scalatest.concurrent.ScalaFutures
import io.circe.syntax.*
import desu.video.common.model.DesuResult
import desu.video.common.quill.model.MysqlContext
import zio.*
import io.getquill.*
import desu.video.common.quill.model.desuVideo.dirMapping

import java.nio.file.Files

class FullTestKitExampleSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with ScalaFutures {
  given LoggingAdapter = Logging.getLogger(system, "akka-http-test")

  given ActorSystem[Nothing] = ActorSystem.wrap(system)

  val testWire = TestWire()
  import testWire.given
  val appConfig      = implicitly[AppConfig]
  val routingMinimal = implicitly[HttpServerRoutingMinimal]
  val mysqlContext   = implicitly[MysqlContext]

  import mysqlContext.{*, given}

  "rootPath should" should {

    "exists" in {
      Files.exists(appConfig.rootPath.futureValue) shouldBe true
    }

    "be a dirctory" in {
      Files.isDirectory(appConfig.rootPath.futureValue) shouldBe true
    }

  }

  "The root file service" should {

    "return a Json when request file list" in {
      Get("/api/desu/rootPathFiles") ~> routingMinimal.route ~> check {
        contentType shouldEqual ContentTypes.`application/json`
        val rootFiles        = appConfig.rootPath.futureValue.toFile.listFiles().to(List).map(_.getName)
        val rootFileToBeTest = RootPathFiles(rootFiles)
        responseAs[DesuResult[RootPathFiles]].data shouldEqual rootFileToBeTest
      }
    }

    "return a Json when request root file name" in {
      val rootFiles = appConfig.rootPath.futureValue.toFile.listFiles().to(List).map(_.getName)
      rootFiles.foreach { fileName =>
        val requestModel = RootFileNameRequest(fileName = fileName)

        Post("/api/desu/rootPathFile", requestModel) ~> routingMinimal.route ~> check {
          contentType shouldEqual ContentTypes.`application/json`

          val dirId = responseAs[DirId]

          inline def fileName = quote {
            query[dirMapping].filter(_.id == lift(dirId.id))
          }
          val fileNameZio = mysqlContext.run(fileName).provideLayer(dataSourceLayer)
          val fileNameF   = Runtime.default.unsafeRunToFuture(fileNameZio)

          val dirNameRow = fileNameF.futureValue

          val confirm =
            for (row <- dirNameRow)
              yield
                val name = for
                  a1 <- io.circe.parser.parse(row.filePath)
                  a2 <- a1.as[List[String]]
                yield a2.head

                DirId(
                  id = row.id,
                  fileName = name.getOrElse(null)
                )

          confirm shouldEqual List(
            DirId(id = dirId.id, fileName = dirId.fileName)
          )
        }
      }
    }

  }

}
