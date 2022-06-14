package desu.video.test.model

import zio.json.*

case class DesuResult[T](isSucceed: Boolean, data: T, message: String)

object DesuResult:
  def data[T](isSucceed: Boolean, data: T, message: String = ""): DesuResult[T] = DesuResult(isSucceed, data, message)
  def message(isSucceed: Boolean, message: String): DesuResult[Option[String]]  = DesuResult(isSucceed, Option.empty[String], message)

  given [T](using JsonEncoder[T]): JsonEncoder[DesuResult[T]] = DeriveJsonEncoder.gen
  given [T](using JsonDecoder[T]): JsonDecoder[DesuResult[T]] = DeriveJsonDecoder.gen
end DesuResult
