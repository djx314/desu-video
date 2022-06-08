package desu.video.test.cases

import zio.*
import sttp.tapir.server.ziohttp.ZioHttpServerOptions
import sttp.tapir.server.interceptor.CustomiseInterceptors
import sttp.tapir.server.interceptor.exception.ExceptionHandler
import sttp.tapir.server.model.ValuedEndpointOutput
import sttp.tapir.PublicEndpoint
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

  transparent inline def value[T, Ploy](inline v: Filling[T, Ploy]): T = v

  given [T, U]: Conversion[T, Filling[T, U]] with
    override def apply(in: T): Filling[T, U] = in

end Filling

object FillingImpl:
  inline given [T]: Filling[Unit, T] = ()
end FillingImpl

class TestEnv:

  private val exceptionHandler = ExceptionHandler.pure[RIO[ZEnv, *]](ctx =>
    Some(ValuedEndpointOutput(stringBody.and(statusCode), (s"failed due to ${ctx.e.getMessage}", StatusCode.InternalServerError)))
  )

  private val customiseOptions: CustomiseInterceptors[RIO[ZEnv, *], ZioHttpServerOptions[ZEnv]] =
    ZioHttpServerOptions.customiseInterceptors.exceptionHandler(exceptionHandler)

  private val m: MonadError[RIO[ZEnv, *]] = new RIOMonadError[ZEnv]

  val stubInterpreter = TapirStubInterpreter(customiseOptions, SttpBackendStub(m))

  type ZIOEndPointType = sttp.capabilities.zio.ZioStreams & sttp.capabilities.Effect[Task] & sttp.capabilities.WebSockets

  def toRequest[E, I, O, U, Ploy](
    endpoint: PublicEndpoint[E, I, O, ZIOEndPointType]
  )(using Filling[E, Ploy]): RIO[ContextUri & SttpClient, Response[DecodeResult[Either[I, O]]]] =
    for
      context <- ZIO.service[ContextUri]
      request = SttpClientInterpreter().toRequest(endpoint, Some(context.uri))
      value   = summon[Filling[E, Ploy]]
      response <- send(request(Filling.value(value)))
    yield response
  end toRequest

end TestEnv

object TestEnv extends TestEnv

def simpleToRequest[E, I, O, U](
  endpoint: PublicEndpoint[E, I, O, TestEnv.ZIOEndPointType]
)(using Filling[E, Filling.type & FillingImpl.type]): RIO[ContextUri & SttpClient, Response[DecodeResult[Either[I, O]]]] =
  TestEnv.toRequest(endpoint)
