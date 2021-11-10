package desu.config

import org.http4s.Uri.Path.Segment
import org.http4s._
import org.http4s.dsl.io._

class AppConfig {

  val FilePageRoot: Path = Root / Segment("api") / Segment("desu")

}
