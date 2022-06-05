package desu.video.common.quill.model

import desu.video.common.quill.model.desuVideo.DesuVideoExtensions
import io.getquill.context.ZioJdbc.DataSourceLayer
import io.getquill.{MySQLDialect, MysqlZioJdbcContext, SnakeCase}

import javax.sql.DataSource
import io.getquill.context.qzio.ImplicitSyntax.Implicit

import java.io.Closeable

class MysqlContext(dataSource: DataSource & Closeable)
    extends MysqlZioJdbcContext[SnakeCase](SnakeCase)
    with DesuVideoExtensions[MySQLDialect, SnakeCase] {
  val dataSourceLayer = DataSourceLayer.fromDataSource(dataSource)
}
