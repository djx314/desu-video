package desu.video.test.cases

import zio.*
import sttp.tapir.{DecodeResult, PublicEndpoint}
import sttp.tapir.ztapir.{query => _, *}
import sttp.tapir.json.circe.*
import sttp.client3.*

import zio.test.{test, *}
import zio.test.Assertion.*
import desu.video.test.model.*
import sttp.tapir.generic.auto.*
import java.nio.file.{Files, Paths}

import sttp.tapir.client.sttp.SttpClientInterpreter
import sttp.client3.httpclient.zio.*
import scala.jdk.CollectionConverters.*
import java.util.stream.Collectors
import io.getquill.*
import desu.video.test.cases.{MysqlJdbcContext => ctx}
import desu.video.common.quill.model.desuVideo.*

import scala.language.implicitConversions
import scala.util.Try
import javax.sql.DataSource
import sttp.model.MediaType

object RootPathFilesTestCase1 extends ZIOSpecDefault:

  import ctx.*

  private val jsonMediaOptString: Option[String] = Some(MediaType.ApplicationJson.toString)

  val rootPathFilesEndpoint: PublicEndpoint[Unit, DesuResult[Option[String]], DesuResult[RootPathFiles], Any] =
    endpoint.get
      .in("api" / "desu" / "rootPathFiles")
      .out(jsonBody[DesuResult[RootPathFiles]])
      .errorOut(jsonBody[DesuResult[Option[String]]])

  val rootPathFileEndpoint: PublicEndpoint[RootFileNameRequest, DesuResult[Option[String]], DirId, Any] =
    endpoint.post
      .in("api" / "desu" / "rootPathFile")
      .in(jsonBody[RootFileNameRequest])
      .out(jsonBody[DirId])
      .errorOut(jsonBody[DesuResult[Option[String]]])

  val rootFileToBeTest: RIO[DesuConfig, List[String]] =
    def forPath(path: String) = Files.list(Paths.get(path)).map(_.toFile.getName).collect(Collectors.toList[String])
    for
      desuConfig <- ZIO.service[DesuConfig]
      path = desuConfig.desu.video.file.rootPath
      names <- ZIO.attempt(forPath(path))
    yield names.asScala.to(List)
  end rootFileToBeTest

  def dirInfoFromId(dirId: Long) =
    inline def fileNameQuery = quote {
      query[dirMapping].filter(_.id == lift(dirId))
    }
    val fileNameZio = ctx.run(fileNameQuery)

    def modelToDirId(dirMapping: dirMapping) =
      def decodeResult = io.circe.parser.decode[List[String]](dirMapping.filePath)
      for
        a1   <- ZIO.fromEither(decodeResult)
        name <- ZIO.attempt(a1.head)
      yield DirId(id = dirMapping.id, fileName = name)
    end modelToDirId

    for
      dirMappings <- fileNameZio
      list = dirMappings.map(modelToDirId)
      coll <- ZIO.collectAll(list)
    yield coll
  end dirInfoFromId

  override def spec = suite("The root path info service")(
    test("should return a json when sending a root info reuqest.") {

      for
        desuConfig <- ZIO.service[DesuConfig]
        response   <- simpleToRequest(rootPathFilesEndpoint)
        names      <- rootFileToBeTest
        DecodeResult.Value(responseModel) = response.body
        model <- ZIO.fromEither(responseModel)
      yield
        val assert2     = RootPathFiles(names)
        val dataAssert  = assert(model.data)(Assertion.equalTo(assert2))
        val mediaAssert = assert(response.contentType)(Assertion.equalTo(jsonMediaOptString))
        dataAssert && mediaAssert

    },
    test("should return a json when sending a root file name.") {

      def assertDirId(model: DirId) =
        val queryAction       = dirInfoFromId(model.id)
        val dirIdDecodeResult = List(DirId(id = model.id, fileName = model.fileName))
        assertZIO(queryAction)(Assertion.equalTo(dirIdDecodeResult))
      end assertDirId

      def singleFileNameResult(name: String) = for
        response <- simpleToRequest(rootPathFileEndpoint)(using RootFileNameRequest(name))
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
        names      <- rootFileToBeTest
        testResult <- fileNameTestResultList(names)
      yield TestResult.all(testResult: _*)

    }
  ).provideCustomLayer(CommonLayer.live)

  end spec

end RootPathFilesTestCase1
