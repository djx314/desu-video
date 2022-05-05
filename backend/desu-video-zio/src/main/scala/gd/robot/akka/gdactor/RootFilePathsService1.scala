package gd.robot.akka.gdactor

import zio._
import zhttp.http._
import zhttp.service.Server

case class HelloWorld1(name: String, age: Int) {
  val println: String = s"名字$name,年龄$age"
}

object HelloWorld1 {
  val println = ZIO.serviceWith[HelloWorld1](_.println)
  val live    = ZLayer.fromFunction(HelloWorld1.apply)
}
