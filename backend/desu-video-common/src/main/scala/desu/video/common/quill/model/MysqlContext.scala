package desu.video.common.quill.model

import cats.effect.{IO => CIO}
import desu.video.common.quill.model.desuVideo.DesuVideoExtensions
import io.getquill.{MySQLDialect, MysqlAsyncContext, SnakeCase}
import scala.concurrent.ExecutionContext
import zio._

object MysqlContext extends MysqlAsyncContext[SnakeCase](SnakeCase, "mysqlDesuDBQuill") with DesuVideoExtensions[MySQLDialect, SnakeCase] {
  def effectIO[T, E <: Effect](io: IO[T, E], transactional: Boolean = false): Task[T] =
    ZIO.fromFuture(implicit ec => performIO(io, transactional))
}
