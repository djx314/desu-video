package desu.routes

import cats.effect._
import cats.syntax.all._
import desu.config.AppConfig
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.twirl._

import java.util.UUID

class IndexPageRoute(appConfig: AppConfig) {

  val webjarPrefix = s"/${appConfig.APPRoot}/${appConfig.WebjarsRoot}"

  val `page-collection.html` = HttpRoutes.of[IO] { case GET -> Root =>
    val parameter = UUID.randomUUID().toString
    Ok(desu.views.html.pageCollection(webjarPrefix)(parameter))
  }

  def routes: HttpRoutes[IO] = `page-collection.html`

}

object IndexPageRoute {
  def build(implicit appConfig: AppConfig): IndexPageRoute = new IndexPageRoute(implicitly)
}
