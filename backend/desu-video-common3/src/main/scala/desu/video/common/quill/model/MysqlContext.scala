package desu.video.common.quill.model

import desu.video.common.quill.model.desuVideo.DesuVideoExtensions
import io.getquill.{MySQLDialect, MysqlJdbcContext, SnakeCase}

object MysqlContext extends MysqlJdbcContext[SnakeCase](SnakeCase, "mysqlDesuDBQuill") with DesuVideoExtensions[MySQLDialect, SnakeCase] /*{
  def effectIO[T, E <: Effect](io: IO[T, E], transactional: Boolean = false): Task[T] =
    ZIO.fromFuture(implicit ec => performIO(io, transactional))
}*/
