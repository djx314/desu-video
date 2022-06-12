package zdesu.service

import zio._

case class HelloWorld(name: String, age: Int) {

  val println: String = s"名字$name,年龄$age"

}

object HelloWorld {

  val println = ZIO.serviceWith[HelloWorld](_.println)
  val live    = ZLayer.fromFunction(HelloWorld.apply _)

}
