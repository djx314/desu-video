package desu.video.akka.routes.test

import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import desu.video.akka.routes.HttpServerRoutingMinimal
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import desu.video.akka.model.{DirId, RootFileNameRequest, RootPathFiles}
import desu.video.common.slick.model.Tables._
import desu.video.common.slick.model.Tables.profile.api._
import org.scalatest.concurrent.ScalaFutures
import io.circe.syntax._

import java.nio.file.Files

class FullTestKitExampleSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with ScalaFutures {

  "rootPath should" should {

    "exists" in {
      Files.exists(TestWire.appConfig.rootPath) shouldBe true
    }

    "be a dirctory" in {
      Files.isDirectory(TestWire.appConfig.rootPath) shouldBe true
    }

  }

  "The root file service" should {

    "return a Json when request file list" in {
      Get("/rootPathFiles") ~> HttpServerRoutingMinimal.route ~> check {
        contentType shouldEqual ContentTypes.`application/json`

        val rootFiles        = TestWire.appConfig.rootPath.toFile.listFiles().to(List).map(_.getName)
        val rootFileToBeTest = RootPathFiles(dirConfirm = true, rootFiles)

        responseAs[RootPathFiles] shouldEqual rootFileToBeTest
      }
    }

    "return a Json when request root file name" in {
      val rootFiles = TestWire.appConfig.rootPath.toFile.listFiles().to(List).map(_.getName)
      rootFiles.foreach { fileName =>
        val requestModel = RootFileNameRequest(fileName = fileName)

        Post("/rootPathFile", requestModel) ~> HttpServerRoutingMinimal.route ~> check {
          contentType shouldEqual ContentTypes.`application/json`

          val dirId      = responseAs[DirId]
          val fileNameF  = TestWire.desuDatabase.db.run(DirMapping.filter(_.id === dirId.id).result)
          val dirNameRow = fileNameF.futureValue.to(List)

          dirNameRow shouldEqual List(DirMappingRow(id = dirId.id, filePath = List(dirId.fileName).asJson.noSpaces))
        }

      }
    }

  }

}
