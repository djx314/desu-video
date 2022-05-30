package desu.video.akka.routes.test

import akka.actor.typed.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import desu.video.akka.model.{DirId, RootFileNameRequest, RootPathFiles}
import desu.video.common.slick.model.Tables._
import desu.video.common.slick.model.Tables.profile.api._
import org.scalatest.concurrent.ScalaFutures
import io.circe.syntax._
import desu.video.common.model.DesuResult

import java.nio.file.Files

class FullTestKitExampleSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with ScalaFutures {
  implicit val logger = Logging.getLogger(system, "akka-http-test")

  implicit val typedSystem = ActorSystem.wrap(system)

  val testWire = TestWire()

  "rootPath should" should {

    "exists" in {
      Files.exists(testWire.appConfig.rootPath.futureValue) shouldBe true
    }

    "be a dirctory" in {
      Files.isDirectory(testWire.appConfig.rootPath.futureValue) shouldBe true
    }

  }

  "The root file service" should {

    "return a Json when request file list" in {
      Get("/api/desu/rootPathFiles") ~> testWire.routingMinimal.route ~> check {
        contentType shouldEqual ContentTypes.`application/json`
        val rootFiles        = testWire.appConfig.rootPath.futureValue.toFile.listFiles().to(List).map(_.getName)
        val rootFileToBeTest = RootPathFiles(rootFiles)
        responseAs[DesuResult[RootPathFiles]].data shouldEqual rootFileToBeTest
      }
    }

    "return a Json when request root file name" in {
      val rootFiles = testWire.appConfig.rootPath.futureValue.toFile.listFiles().to(List).map(_.getName)
      rootFiles.foreach { fileName =>
        val requestModel = RootFileNameRequest(fileName = fileName)

        Post("/api/desu/rootPathFile", requestModel) ~> testWire.routingMinimal.route ~> check {
          contentType shouldEqual ContentTypes.`application/json`

          val dirId      = responseAs[DirId]
          val fileNameF  = testWire.desuDatabase.db.run(DirMapping.filter(_.id === dirId.id).to[List].result)
          val dirNameRow = fileNameF.futureValue

          val confirm =
            for (row <- dirNameRow)
              yield {
                val name = for {
                  a1 <- io.circe.parser.parse(row.filePath)
                  a2 <- a1.as[List[String]]
                } yield a2.head
                DirId(
                  id = row.id,
                  fileName = name.getOrElse(null)
                )
              }

          confirm shouldEqual List(
            DirId(id = dirId.id, fileName = dirId.fileName)
          )
        }
      }
    }

  }

}
