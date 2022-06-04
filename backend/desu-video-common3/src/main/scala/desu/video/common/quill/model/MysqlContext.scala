package desu.video.common.quill.model

import desu.video.common.quill.model.desuVideo.DesuVideoExtensions
import io.getquill.{MySQLDialect, MysqlZioJdbcContext, SnakeCase}

import javax.sql.DataSource
import io.getquill.context.qzio.ImplicitSyntax.Implicit
import java.io.Closeable

class MysqlContext(ds: DataSource with Closeable)
    extends MysqlZioJdbcContext[SnakeCase](SnakeCase)
    with DesuVideoExtensions[MySQLDialect, SnakeCase] {
  given Implicit[DataSource with Closeable] = Implicit(ds)
}
