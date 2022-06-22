package desu.video.test.cases.mainapp

import desu.video.common.quill.model.desuVideo.DesuVideoExtensions
import io.getquill.context.ZioJdbc.DataSourceLayer
import io.getquill.{MySQLDialect, MysqlZioJdbcContext, SnakeCase}

import javax.sql.DataSource
import io.getquill.context.qzio.ImplicitSyntax.Implicit

import java.io.Closeable
import io.getquill.MysqlJdbcContext

class MysqlJdbcContext extends MysqlZioJdbcContext[SnakeCase](SnakeCase) with DesuVideoExtensions[MySQLDialect, SnakeCase]:
end MysqlJdbcContext

object MysqlJdbcContext extends MysqlJdbcContext
