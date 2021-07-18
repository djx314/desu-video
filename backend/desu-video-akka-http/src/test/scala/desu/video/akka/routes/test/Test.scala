package desu.video.akka.routes.test

import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import desu.video.akka.routes.HttpServerRoutingMinimal
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class FullTestKitExampleSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {

  "The service" should {

    "return a Json when request file list" in {
      Get("/hello") ~> HttpServerRoutingMinimal.route ~> check {
        contentType shouldEqual ContentTypes.`application/json`
      }
    }

  }

}
