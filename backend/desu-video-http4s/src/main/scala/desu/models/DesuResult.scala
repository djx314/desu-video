package desu.models

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.syntax.*

case class DesuResult[T](isSucceed: Boolean, data: T, message: String)

object DesuResult:
  def data[T](isSucceed: Boolean, data: T, message: String = ""): DesuResult[T] = DesuResult(isSucceed, data, message)
  def message(isSucceed: Boolean, message: String): DesuResult[Option[String]]  = DesuResult(isSucceed, Option.empty[String], message)

  given [T](using Encoder[T]): Encoder.AsObject[DesuResult[T]] = deriveEncoder
  given [T](using Decoder[T]): Decoder[DesuResult[T]]          = deriveDecoder
end DesuResult

object Test:

  val test1 = DesuResult.data(isSucceed = true, data = List("22"))
  val json1 = test1.asJson // success

  val test2 = DesuResult(isSucceed = false, data = List("22"), message = "empty").asJson // compile error

  // val test3 = DesuResult.data(isSucceed = false, data = List("22")).asJson // same issue

end Test
