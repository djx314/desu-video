package assist.controllers

import sttp.tapir.ztapir._
import sttp.model._

object TapirEndpoint {
  val encodeEndpoint = endpoint.in("isEncoding").method(Method.GET).in(query[String]("uuid")).out(plainBody[Boolean])

  import sttp.tapir.docs.openapi._
  import sttp.tapir.openapi._
  val docs = List(encodeEndpoint).toOpenAPI(Info(title = "爬虫", version = "1.0", description = Option("爬虫")))
}
