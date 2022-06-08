package desu.video.test.cases

import zio.*
import sttp.tapir.{DecodeResult, PublicEndpoint}
import sttp.tapir.ztapir.*
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

import scala.language.implicitConversions

object RootPathFilesTestCase1 extends ZIOSpecDefault:

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
      val testAction =
        for desuConfig <- ZIO.service[DesuConfig]
        yield
          val coll =
            for fileName <- rootFileToBeTest(desuConfig.desu.video.file.rootPath)
            yield for response <- simpleToRequest(rootPathFileEndpoint)(using RootFileNameRequest(fileName))
            yield
              val assert1     = response.body
              val dirIdResult = assert1.map(_.map(_.id))
              val assert2     = DecodeResult.Value(Right(rootFileToBeTest(desuConfig.desu.video.file.rootPath)))
              assert(assert1)(Assertion.equalTo(assert2))

          ZIO.collectAll(coll)

      for
        assertion1 <- testAction
        assertion2 <- assertion1
      yield TestResult.all(assertion2: _*)
    }
  ).provideCustomLayer(ZEnv.live ++ HttpClientZioBackend.layer() ++ DesuConfigModel.layer ++ ContextUri.live1)

  end spec

end RootPathFilesTestCase1
