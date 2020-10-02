package tapir_controllers

import sttp.model._
import sttp.tapir.ztapir._
import sttp.tapir.json.circe._

object TapirEndpoint {
  val encodeEndpoint = endpoint.in("isEncoding").method(Method.GET).in(query[String]("uuid")).out(jsonBody[Boolean])

  import sttp.tapir.docs.openapi._
  import sttp.tapir.openapi._
  val docs = List(encodeEndpoint).toOpenAPI(Info(title = "爬虫", version = "1.0", description = Option("爬虫")))
}
