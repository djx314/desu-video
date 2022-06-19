package desu.video.test.model

case class DesuResult[T](isSucceed: Boolean, data: T, message: String)

object DesuResult:
  def data[T](isSucceed: Boolean, data: T, message: String = ""): DesuResult[T] = DesuResult(isSucceed, data, message)
  def message(isSucceed: Boolean, message: String): DesuResult[Option[String]]  = DesuResult(isSucceed, Option.empty[String], message)
end DesuResult
