package desu.routes

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._

class FilePageRoute {

  val firstRoute = HttpRoutes.of[IO] { case GET -> Root / "hello" / name =>
    Ok(name)
  }

}
