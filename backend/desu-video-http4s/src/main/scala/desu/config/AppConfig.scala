package desu.config

import org.http4s.Uri.Path.Segment
import org.http4s.dsl.io._

import java.nio.file.{Path => JPath, Paths}
import sttp.tapir._

class AppConfig {

  val FilePageRoot: Path = Root / Segment("api") / Segment("desu")

  val rootFilePath: JPath = Paths.get("d:", "xlxz")

}

object AppConfig {
  val filePageRoot: PublicEndpoint[Unit, Unit, Unit, Any] = endpoint.in("api" / "desu")
}
