package desu.video.test.cases.mainapp

import zio.*
import sttp.tapir.server.ziohttp.ZioHttpServerOptions
import sttp.tapir.server.interceptor.CustomiseInterceptors
import sttp.tapir.server.interceptor.exception.ExceptionHandler
import sttp.tapir.server.model.ValuedEndpointOutput
import sttp.tapir.ztapir.*
import sttp.tapir.Endpoint
import sttp.model.StatusCode
import sttp.monad.MonadError
import sttp.tapir.server.stub.TapirStubInterpreter
import sttp.client3.testing.SttpBackendStub
import sttp.tapir.client.sttp.SttpClientInterpreter
import sttp.client3.httpclient.zio.*
import sttp.client3.*
import sttp.model.*
import sttp.tapir.{DecodeResult, PublicEndpoint}
import scala.language.implicitConversions

opaque type Filling[T, Ploy] = T

object Filling:
  inline def value[T, Ploy](inline v: Filling[T, Ploy]): T = v
  inline given [T, U]: Conversion[T, Filling[T, U]]        = identity
end Filling

object FillingImpl:
  inline given [T]: Filling[Unit, T] = ()
end FillingImpl

private class SttpEnvImpl:

  private val exceptionHandler = ExceptionHandler.pure[Task](ctx =>
    Some(ValuedEndpointOutput(stringBody.and(statusCode), (s"failed due to ${ctx.e.getMessage}", StatusCode.InternalServerError)))
  )

  private val customiseOptions: CustomiseInterceptors[Task, ZioHttpServerOptions[Any]] =
    ZioHttpServerOptions.customiseInterceptors.exceptionHandler(exceptionHandler)

  private val m: MonadError[Task] = new RIOMonadError[Any]

  val stubInterpreter = TapirStubInterpreter(customiseOptions, SttpBackendStub(m))

  def toRequest[E, I, O, U, Ploy](
    endpoint: PublicEndpoint[E, I, O, __SttpZIORequestEndpointType]
  )(using Filling[E, Ploy]): RIO[ContextUri & SttpClient, Response[DecodeResult[Either[I, O]]]] =
    for
      context <- ZIO.service[ContextUri]
      request = SttpClientInterpreter().toRequest(endpoint, Some(context.uri))
      value   = summon[Filling[E, Ploy]]
      response <- send(request(Filling.value(value)))
    yield response
  end toRequest

end SttpEnvImpl

private object SttpEnvImpl extends SttpEnvImpl

type __SttpZIORequestEndpointType = sttp.capabilities.zio.ZioStreams & sttp.capabilities.Effect[Task] & sttp.capabilities.WebSockets

def simpleToRequest[E, I, O](
  endpoint: PublicEndpoint[E, I, O, __SttpZIORequestEndpointType]
)(using Filling[E, Filling.type & FillingImpl.type]): RIO[ContextUri & SttpClient, Response[DecodeResult[Either[I, O]]]] =
  SttpEnvImpl.toRequest(endpoint)
