package desu.video.test.cases

import zio.*
import sttp.tapir.{DecodeResult, PublicEndpoint}
import sttp.tapir.ztapir.{query => _, *}
import sttp.tapir.json.circe.*
import sttp.client3.*

import zio.test.{test, *}
import zio.test.Assertion.*
import desu.video.test.model.*
import java.nio.file.{Files, Paths}

import io.getquill.*
import desu.video.common.quill.model.desuVideo.*

import scala.language.implicitConversions
import sttp.model.MediaType
import desu.video.test.cases.endpoints.TestEndpoint
import desu.video.test.cases.services.{ResolveFileNameService, RootPathFileServices}
import mainapp.*

object RootPathFilesTestCase1 extends ZIOSpecDefault:

  private val jsonMediaOptString: Option[String] = Some(MediaType.ApplicationJson.toString)

  override def spec = suite("The root path info service")(
    test("should return a json when sending a root info reuqest.") {

      for
        response <- simpleToRequest(TestEndpoint.rootPathFilesEndpoint)
        names    <- RootPathFileServices.resloveRootFiles
        DecodeResult.Value(responseModel) = response.body
        model <- ZIO.fromEither(responseModel)
      yield
        val assert2         = RootPathFiles(names)
        val dataAssert      = assert(model.data)(Assertion.equalTo(assert2))
        val mediaAssert     = assert(response.contentType)(Assertion.equalTo(jsonMediaOptString))
        val successedAssert = assert(model.isSucceed)(Assertion.equalTo(true))
        dataAssert && mediaAssert && successedAssert

    },
    test("should return a json when sending a root file name.") {

      def assertDirId(model: DirId) =
        val queryAction       = ResolveFileNameService.dirInfoFromId(model.id)
        val dirIdDecodeResult = List(DirId(id = model.id, fileName = model.fileName))
        assertZIO(queryAction)(Assertion.equalTo(dirIdDecodeResult))
      end assertDirId

      def singleFileNameResult(name: String) = for
        response <- simpleToRequest(TestEndpoint.rootPathFileEndpoint)(using RootFileNameRequest(name))
        DecodeResult.Value(responseValue) = response.body
        model       <- ZIO.fromEither(responseValue)
        modelAssert <- assertDirId(model)
      yield
        val mediaTypeAssert = assert(response.contentType)(Assertion.equalTo(jsonMediaOptString))
        modelAssert && mediaTypeAssert
      end singleFileNameResult

      def fileNameTestResultList(names: List[String]) =
        val assertSet = names.map(singleFileNameResult)
        ZIO.collectAll(assertSet)
      end fileNameTestResultList

      for
        names      <- RootPathFileServices.resloveRootFiles
        testResult <- fileNameTestResultList(names)
      yield TestResult.all(testResult: _*)

    }
  ).provide(CommonLayer.live, RootPathFileServices.layer, ResolveFileNameService.layer)

  end spec

end RootPathFilesTestCase1
