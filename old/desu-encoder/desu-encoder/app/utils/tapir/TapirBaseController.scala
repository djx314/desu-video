package utils.tapir

import akka.stream.Materializer
import play.api.routing.Router.Routes
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.play._
import sttp.tapir.ztapir._
import zio._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

trait TapirComponents {

  val materializer: Materializer
  val ec: ExecutionContext

}

class TapirComponentsImpl(val materializer: Materializer, val ec: ExecutionContext) extends TapirComponents

trait TapirComponentsApplication {

  def materializer: Materializer
  def executionContext: ExecutionContext
  lazy val tapirComponents: TapirComponents = new TapirComponentsImpl(materializer, executionContext)

}

abstract class TapirBaseController(tapirComponents: TapirComponents) {
  implicit val materializer: Materializer         = tapirComponents.materializer
  implicit val executionContext: ExecutionContext = tapirComponents.ec

  class ZLayerToPlayRoute[R](val zlayer: ZLayer[ZEnv, Throwable, R]) {
    def toRoute[I, E, O](e: ZServerEndpoint[R, I, E, O]): Routes = TapirBaseController.toFutureEndpointRLayer(zlayer, e).toRoute
    def toRoutes(e: PlayRoute[R], e1: PlayRoute[R]*): Routes =
      e1.toList.foldLeft(e.route(zlayer, implicitly, implicitly))((b1, b2) => b1.orElse(b2.route(zlayer, implicitly, implicitly)))
  }
  def zlayerToRoute[R](zlayer: ZLayer[ZEnv, Throwable, R]): ZLayerToPlayRoute[R] = new ZLayerToPlayRoute(zlayer)
}

object TapirBaseController {

  def toFutureEndpointRLayer[R, I, E, O](zLayer: ZLayer[ZEnv, Throwable, R], se: ZServerEndpoint[R, I, E, O]): ServerEndpoint[I, E, O, Any, Future] =
    ServerEndpoint(se.endpoint, _ => (i: I) => Runtime.default.unsafeRunToFuture(se.logic(new ZIOTapirMonadError[R])(i).provideLayer(zLayer)))

}

abstract class PlayRoute[I] {
  def route(zlayer: ZLayer[ZEnv, Throwable, I], ma: Materializer, serverOptions: PlayServerOptions): Routes
}

object PlayRoute {
  implicit def ZServerEndpointToPlayRouteImplicit1[R](i1: ZServerEndpoint[_ >: R, _, _, _]): PlayRoute[R] =
    new PlayRoute[R] {
      override def route(zlayer: ZLayer[zio.ZEnv, Throwable, R], ma: Materializer, serverOptions: PlayServerOptions): Routes = {
        implicit val m = ma
        implicit val e = serverOptions
        TapirBaseController.toFutureEndpointRLayer(zlayer, i1).toRoute
      }
    }
  implicit def routeToPlayRouteImplicit2[R](i1: Routes): PlayRoute[R] =
    new PlayRoute[R] {
      override def route(zlayer: ZLayer[zio.ZEnv, Throwable, R], ma: Materializer, serverOptions: PlayServerOptions): Routes = i1
    }
}
