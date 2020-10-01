package assist.controllers

import sttp.tapir.server.play._
import sttp.tapir.swagger.play.SwaggerPlay
import sttp.tapir.ztapir._
import utils.tapir.{TapirBaseController, TapirComponents}

class EncodeTapirLink(tapirComponents: TapirComponents, appContenxt: wiring.commons.HttpContext) extends TapirBaseController(tapirComponents) {

  val zioEndpoint = zlayerToRoute(appContenxt)

  val encodeRoute = TapirRoute.encodeEndpoint.zServerLogic(TapirController.isEncoding _)

  import sttp.tapir.openapi.circe.yaml._

  private val docName = "docs"
  val swaggerPlay     = new SwaggerPlay(TapirRoute.docs.toYaml, yamlName = s"${docName}.yaml", contextPath = docName).routes

  val listRoute = zioEndpoint.toRoutes(encodeRoute, swaggerPlay)

}
