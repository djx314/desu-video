package zdesu.endpoint

import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.generic.auto._

import zdesu.model.User

object UserEndpoint {

  val userEndpoint: PublicEndpoint[Unit, String, User, Any]   = endpoint.get.in("text").errorOut(stringBody).out(jsonBody[User])
  val userEndpoint11: PublicEndpoint[Unit, String, User, Any] = endpoint.get.in("text11").errorOut(stringBody).out(jsonBody[User])

}
