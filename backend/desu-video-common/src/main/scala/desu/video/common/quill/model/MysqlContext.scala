package desu.video.common.quill.model

import cats.effect.{IO => CIO}
import desu.video.common.quill.model.desuVideo.DesuVideoExtensions
import io.getquill.{MySQLDialect, MysqlAsyncContext, SnakeCase}
import scala.concurrent.ExecutionContext

object MysqlContext extends MysqlAsyncContext[SnakeCase](SnakeCase, "mysqlDesuDBQuill") with DesuVideoExtensions[MySQLDialect, SnakeCase] {
  def effectIO[T, E <: Effect](io: IO[T, E], transactional: Boolean = false)(implicit ec: ExecutionContext): CIO[T] =
    CIO.fromFuture(CIO(performIO(io, transactional)))
}
