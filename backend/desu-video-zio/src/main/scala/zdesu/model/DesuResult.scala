package zdesu.model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class DesuResult[T](isSucceed: Boolean, data: T, message: String)

object DesuResult {
  def data[T](isSucceed: Boolean, data: T, message: String = ""): DesuResult[T] = DesuResult(isSucceed, data, message)
  def message(isSucceed: Boolean, message: String): DesuResult[Option[String]]  = DesuResult(isSucceed, Option.empty[String], message)

  implicit def _desuResultEncoder[T](implicit e: Encoder[T]): Encoder[DesuResult[T]] = deriveEncoder
  implicit def _desuResultDecoder[T](implicit e: Decoder[T]): Decoder[DesuResult[T]] = deriveDecoder
}
