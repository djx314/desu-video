package desu.video.akka.routes.test

import zio.*
import sttp.tapir.PublicEndpoint
import sttp.tapir.ztapir.*
import sttp.tapir.json.circe.*
import sttp.client3.*
import sttp.tapir.DecodeResult

import zio.test.{test, *}
import zio.test.Assertion.*
import desu.video.test.model.*
import sttp.tapir.generic.auto.*

import sttp.tapir.client.sttp.SttpClientInterpreter
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend

object Test1 extends ZIOSpecDefault:

  val someEndpoint: PublicEndpoint[Unit, DesuResult[Option[String]], DesuResult[RootPathFiles], Any] =
    endpoint.get
      .in("api" / "desu" / "rootPathFiles")
      .out(jsonBody[DesuResult[RootPathFiles]])
      .errorOut(jsonBody[DesuResult[Option[String]]])

  // given
  val stub = TestEnv.stubInterpreter.whenEndpoint(someEndpoint).thenThrowException(new RuntimeException("error")).backend()

  // when
  override def spec = suite("The root path info service")(
    test("should return a json when sending a root info reuqest.")(
      for {
        backend <- AsyncHttpClientZioBackend()
        response = SttpClientInterpreter().toClient(someEndpoint, Some(uri"http://localhost:8080"), backend)
        s <- response(())
      } yield assert(s)(Assertion.equalTo(DecodeResult.Value(Right("aa"))))
    )
  ).provideCustomLayer(ZEnv.live)

end Test1
