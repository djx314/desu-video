package zdesu.model

import julienrf.json.derived
import play.api.libs.json.{OWrites, Reads, Writes}

case class DesuResult[T](isSucceed: Boolean, data: T, message: String)

object DesuResult {
  def data[T](isSucceed: Boolean, data: T, message: String = ""): DesuResult[T] = DesuResult(isSucceed, data, message)
  def message(isSucceed: Boolean, message: String): DesuResult[Option[String]]  = DesuResult(isSucceed, Option.empty[String], message)

  implicit def _desuResultEncoder[T](implicit e: Reads[T]): Reads[DesuResult[T]]    = derived.reads()
  implicit def _desuResultDecoder[T](implicit e: Writes[T]): OWrites[DesuResult[T]] = derived.owrites()
}
