package desu.models

import io.circe.{Decoder, Encoder}

case class DesuResult[T](isSucceed: Boolean, data: T, message: String)

object DesuResult:
  def data[T](isSucceed: Boolean, data: T, message: String = "")(using Encoder[T]): DesuResult[T] = DesuResult(isSucceed, data, message)
  def message(isSucceed: Boolean, message: String): DesuResult[Option[String]] = DesuResult(isSucceed, Option.empty[String], message)

  given [T](using Encoder[T]): Encoder.AsObject[DesuResult[T]] = Encoder.AsObject.derived
  given [T](using Decoder[T]): Decoder[DesuResult[T]]          = Decoder.derived
end DesuResult
