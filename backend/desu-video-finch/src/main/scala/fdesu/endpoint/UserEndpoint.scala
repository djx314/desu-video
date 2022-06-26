package fdesu.endpoint

import io.finch._
import cats.effect._, cats.effect.implicits._, cats.effect.syntax._
import cats._, cats.implicits._, cats.syntax._
import com.twitter.finagle.Http
import com.twitter.util.Await

object Main extends App with Endpoint.Module[IO] {
  IO.pure(2).foldMapK()
  val api: Endpoint[IO, String] = get("hello") { Ok("Hello, World!") }
  Await.ready(Http.server.serve(":8080", api.toServiceAs[Text.Plain]))
}
