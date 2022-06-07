package desu.video.akka.routes.test

import zio.*
import sttp.tapir.{DecodeResult, PublicEndpoint}
import sttp.tapir.ztapir.*
import sttp.tapir.json.circe.*
import sttp.client3.*

import zio.test.{test, *}
import zio.test.Assertion.*
import desu.video.test.model.*
import sttp.tapir.generic.auto.*
import java.nio.file.Paths

import sttp.tapir.client.sttp.SttpClientInterpreter
import sttp.client3.httpclient.zio.*

object Test1 extends ZIOSpecDefault:

  val someEndpoint: PublicEndpoint[Unit, DesuResult[Option[String]], DesuResult[RootPathFiles], Any] =
    endpoint.get
      .in("api" / "desu" / "rootPathFiles")
      .out(jsonBody[DesuResult[RootPathFiles]])
      .errorOut(jsonBody[DesuResult[Option[String]]])

  val rootFileNames    = Paths.get("d:/xlxz").toFile.listFiles().to(List).map(_.getName)
  val rootFileToBeTest = RootPathFiles(rootFileNames)

  // given
  val stub = TestEnv.stubInterpreter.whenEndpoint(someEndpoint).thenThrowException(new RuntimeException("error")).backend()

  // when
  override def spec = suite("The root path info service")(test("should return a json when sending a root info reuqest.") {

    val request = SttpClientInterpreter().toRequest(someEndpoint, Some(uri"http://localhost:8080"))

    for (response <- send(request(())))
      yield
        val assert1 = response.body.map(_.map(_.data))
        val assert2 = DecodeResult.Value(Right(rootFileToBeTest))
        assert(assert1)(Assertion.equalTo(assert2))

  }).provideCustomLayer(ZEnv.live ++ HttpClientZioBackend.layer())

end Test1
