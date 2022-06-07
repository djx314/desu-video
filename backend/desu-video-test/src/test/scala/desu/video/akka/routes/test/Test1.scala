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

object RootPathFilesTestCase1 extends ZIOSpecDefault:

  val someEndpoint: PublicEndpoint[Unit, DesuResult[Option[String]], DesuResult[RootPathFiles], Any] =
    endpoint.get
      .in("api" / "desu" / "rootPathFiles")
      .out(jsonBody[DesuResult[RootPathFiles]])
      .errorOut(jsonBody[DesuResult[Option[String]]])

  def rootFileToBeTest(path: String): RootPathFiles = {
    val files = Files.list(Paths.get(path)).map(_.toFile.getName).collect(Collectors.toList[String])
    RootPathFiles(files.asScala.to(List))
  }

  override def spec = suite("The root path info service")(test("should return a json when sending a root info reuqest.") {

    val request = SttpClientInterpreter().toRequest(someEndpoint, Some(uri"http://localhost:8080"))

    for {
      desuConfig <- ZIO.service[DesuConfig]
      response   <- send(request(()))
    } yield
      val assert1 = response.body.map(_.map(_.data))
      val assert2 = DecodeResult.Value(Right(rootFileToBeTest(desuConfig.desu.video.file.rootPath)))
      assert(assert1)(Assertion.equalTo(assert2))

  }).provideCustomLayer(ZEnv.live ++ HttpClientZioBackend.layer() ++ DesuConfigModel.layer)

end RootPathFilesTestCase1
