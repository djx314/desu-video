package desu.video.akka.routes.test

import zio.*
import sttp.tapir.server.ziohttp.ZioHttpServerOptions
import sttp.tapir.server.interceptor.exception.ExceptionHandler
import scala.concurrent.Future
import sttp.tapir.server.model.ValuedEndpointOutput
import sttp.model.StatusCode
import sttp.tapir.PublicEndpoint
import sttp.tapir.server.interceptor.CustomiseInterceptors
import sttp.client3.testing.SttpBackendStub
import sttp.tapir.server.stub.TapirStubInterpreter
import sttp.tapir.ztapir.*
import sttp.client3.*
import sttp.monad.MonadError

import zio.test.{test, *}
import zio.test.Assertion.*

object Test1 extends ZIOSpecDefault:

  val exceptionHandler = ExceptionHandler.pure[[x] =>> RIO[ZEnv, x]](ctx =>
    Some(ValuedEndpointOutput(stringBody.and(statusCode), (s"failed due to ${ctx.e.getMessage}", StatusCode.InternalServerError)))
  )

  val customOptions: CustomiseInterceptors[[x] =>> RIO[ZEnv, x], ZioHttpServerOptions[ZEnv]] =
    ZioHttpServerOptions.customiseInterceptors.exceptionHandler(exceptionHandler)

  val someEndpoint: PublicEndpoint[Unit, String, String, Any] = endpoint.get.in("api").out(stringBody).errorOut(stringBody)

  val m: MonadError[[x] =>> RIO[ZEnv, x]] = new RIOMonadError[ZEnv]

  // given
  val stub = TapirStubInterpreter(customOptions, SttpBackendStub(m))
    .whenEndpoint(someEndpoint)
    .thenThrowException(new RuntimeException("error"))
    .backend()

  // when

  override def spec = suite("A Suite")(
    test("aa")(
      sttp.client3.basicRequest
        .get(uri"http://test.com/api")
        .send(stub)
        // then
        .map(s => assert(s.body)(Assertion.equalTo(Left("failed due to error"))))
    )
  ).provideCustomLayer(ZEnv.live)

end Test1
