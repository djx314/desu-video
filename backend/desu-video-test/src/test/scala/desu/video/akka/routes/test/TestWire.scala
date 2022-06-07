package desu.video.akka.routes.test

import zio.*
import sttp.tapir.server.ziohttp.ZioHttpServerOptions
import sttp.tapir.server.interceptor.CustomiseInterceptors
import sttp.tapir.server.interceptor.exception.ExceptionHandler
import sttp.tapir.server.model.ValuedEndpointOutput
import sttp.tapir.PublicEndpoint
import sttp.tapir.ztapir.*
import sttp.model.StatusCode
import sttp.monad.MonadError
import sttp.tapir.server.stub.TapirStubInterpreter
import sttp.client3.testing.SttpBackendStub

class TestEnv {

  val exceptionHandler = ExceptionHandler.pure[[x] =>> RIO[ZEnv, x]](ctx =>
    Some(ValuedEndpointOutput(stringBody.and(statusCode), (s"failed due to ${ctx.e.getMessage}", StatusCode.InternalServerError)))
  )

  val customOptions: CustomiseInterceptors[[x] =>> RIO[ZEnv, x], ZioHttpServerOptions[ZEnv]] =
    ZioHttpServerOptions.customiseInterceptors.exceptionHandler(exceptionHandler)

  val m: MonadError[[x] =>> RIO[ZEnv, x]] = new RIOMonadError[ZEnv]

  val stubInterpreter = TapirStubInterpreter(customOptions, SttpBackendStub(m))

}

object TestEnv extends TestEnv
