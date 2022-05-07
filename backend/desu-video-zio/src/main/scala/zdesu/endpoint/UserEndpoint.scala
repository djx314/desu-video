package zdesu.endpoint

import sttp.tapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.given

import zdesu.model.User

object UserEndpoint:

  val userEndpoint: PublicEndpoint[Unit, String, User, Any]   = endpoint.get.in("text").errorOut(stringBody).out(jsonBody[User])
  val userEndpoint11: PublicEndpoint[Unit, String, User, Any] = endpoint.get.in("text11").errorOut(stringBody).out(jsonBody[User])

end UserEndpoint
