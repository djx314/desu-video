package fdesu.endpoint

import io.finch._, cats.effect.IO
import com.twitter.finagle.Http
import com.twitter.util.Await

object Main extends App with Endpoint.Module[IO] {
  val api: Endpoint[IO, String] = get("hello") { Ok("Hello, World!") }
  Await.ready(Http.server.serve(":8080", api.toServiceAs[Text.Plain]))
}
