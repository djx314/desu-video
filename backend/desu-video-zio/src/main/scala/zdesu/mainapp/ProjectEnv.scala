package zdesu.mainapp

import slick.dbio.{DBIO, DBIOAction}
import slick.jdbc.JdbcBackend
import slick.jdbc.JdbcBackend.Database
import zio.config._
import zio.config.magnolia.descriptor
import zio.config.typesafe._
import zio._

import scala.concurrent.ExecutionContext

case class DesuConfig(desu: VideoConfig)
case class VideoConfig(video: FileConfig)
case class FileConfig(file: RootPath)
case class RootPath(rootPath: String)

object DesuConfigModel {
  private val desuConfigAutomatic = descriptor[DesuConfig]
  val layer                       = TypesafeConfig.fromResourcePath(desuConfigAutomatic)
}

trait SlickDBAction {

  val db: JdbcBackend#Database

  def runWith[T](dbioE: ExecutionContext => DBIO[T]): Task[T] = ZIO.fromFuture(dbioE.andThen(db.run))

}

object SlickDBAction {

  val dbLive: RLayer[Scope, JdbcBackend#Database] = {
    val dbZIO =
      ZIO.acquireRelease(ZIO.attempt(Database.forConfig("mysqlDesuSlickDB"): JdbcBackend#Database))(s => ZIO.attempt(s.close()).orDie)
    ZLayer.fromZIO(dbZIO)
  }
  val live: RLayer[JdbcBackend#Database, SlickDBAction] = ZLayer.fromFunction((s: JdbcBackend#Database) =>
    new SlickDBAction {
      override val db: JdbcBackend#Database = s
    }
  )

  def runWith[T](dbioE: ExecutionContext => DBIO[T]): RIO[SlickDBAction, T] = ZIO.serviceWithZIO[SlickDBAction](_.runWith(dbioE))
  def run[T](dbio: DBIO[T]): RIO[SlickDBAction, T]                          = runWith(_ => dbio)

}
