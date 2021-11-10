package desu.video.common.quill.model

import desu.video.common.quill.model.desuVideo.DesuVideoExtensions
import io.getquill.{MySQLDialect, MysqlAsyncContext, SnakeCase}

object MysqlContext extends MysqlAsyncContext[SnakeCase](SnakeCase, "mysqlDesuDBQuill") with DesuVideoExtensions[MySQLDialect, SnakeCase]
