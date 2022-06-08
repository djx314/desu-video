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

object RootPathFilesTestCase1 extends ZIOSpecDefault:

  import ctx.*

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

  def rootFileToBeTest(path: String): List[String] =
    val files = Files.list(Paths.get(path)).map(_.toFile.getName).collect(Collectors.toList[String])
    files.asScala.to(List)
  end rootFileToBeTest

  def dirInfoFromId(dirId: Long) =
    inline def fileNameQuery = quote {
      query[dirMapping].filter(_.id == lift(dirId))
    }
    val fileNameZio = ctx.run(fileNameQuery)

    def modelToDirId(dirMapping: dirMapping) = for
      a1   <- io.circe.parser.decode[List[String]](dirMapping.filePath)
      name <- Try(a1.head).toEither
    yield DirId(
      id = dirMapping.id,
      fileName = name
    )
    end modelToDirId

    def modelEitherToZIO(models: List[dirMapping]) = for model <- models yield ZIO.fromEither(modelToDirId(model))

    for
      fileName <- fileNameZio
      dirIds   <- ZIO.collectAll(modelEitherToZIO(fileName))
    yield dirIds
  end dirInfoFromId

  override def spec = suite("The root path info service")(
    test("should return a json when sending a root info reuqest.") {

      for
        desuConfig <- ZIO.service[DesuConfig]
        response   <- simpleToRequest(rootPathFilesEndpoint)
      yield
        val assert1 = response.body.map(_.map(_.data))
        val names   = rootFileToBeTest(desuConfig.desu.video.file.rootPath)
        val assert2 = DecodeResult.Value(Right(RootPathFiles(names)))
        assert(assert1)(Assertion.equalTo(assert2))

    },
    test("should return a json when sending a root file name.") {

      def testResultGen(name: String) =
        ZIO.flatten(
          for response <- simpleToRequest(rootPathFileEndpoint)(using RootFileNameRequest(name))
          yield
            val assert1 = response.body.match {
              case DecodeResult.Value(Right(value)) => value
              case s                                => throw IllegalArgumentException(s"Error Response Value.${s}")
            }

            val queryAction       = dirInfoFromId(assert1.id)
            val dirIdDecodeResult = List(DirId(id = assert1.id, fileName = assert1.fileName))
            assertZIO(queryAction)(Assertion.equalTo(dirIdDecodeResult))
        )
      end testResultGen

      def fileNameTestResultList(names: List[String]) = ZIO.collectAll(for name <- names yield testResultGen(name))

      val testAction =
        for
          desuConfig <- ZIO.service[DesuConfig]
          testResult <- fileNameTestResultList(rootFileToBeTest(desuConfig.desu.video.file.rootPath))
        yield testResult
      end testAction

      for assertion1 <- testAction
      yield TestResult.all(assertion1: _*)

    }
  ).provideCustomLayer(CommonLayer.live)

  end spec

end RootPathFilesTestCase1
