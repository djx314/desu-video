package desu.video.akka.routes.test

import akka.actor.typed.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport.*
import desu.video.akka.config.AppConfig
import desu.video.akka.model.{DirId, RootFileNameRequest, RootPathFiles}
import desu.video.akka.routes.HttpServerRoutingMinimal
import org.scalatest.concurrent.ScalaFutures
import io.circe.syntax.*
import desu.video.common.model.DesuResult
import desu.video.common.quill.model.MysqlContext
import zio.*
import io.getquill.*
import desu.video.common.quill.model.desuVideo.dirMapping

import java.nio.file.Files

class FullTestKitExampleSpec extends AnyWordSpec with Matchers with ScalatestRouteTest with ScalaFutures {
  "dev.zio" % "zio-config_3" % "3.0.0-RC9"

}
