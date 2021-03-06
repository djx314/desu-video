package desu.video.common.slick.model
// AUTO-GENERATED Slick data model for table DirMapping
trait DirMappingTable {

  self:TablesRoot  =>

  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}
  /** Entity class storing rows of table DirMapping
   *  @param id Database column id SqlType(INT UNSIGNED), AutoInc, PrimaryKey
   *  @param filePath Database column file_path SqlType(VARCHAR), Length(1000,true)
   *  @param parentId Database column parent_id SqlType(INT) */
  case class DirMappingRow(id: Long, filePath: String, parentId: Int)
  /** GetResult implicit for fetching DirMappingRow objects using plain SQL queries */
  implicit def GetResultDirMappingRow(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[DirMappingRow] = GR{
    prs => import prs._
    DirMappingRow.tupled((<<[Long], <<[String], <<[Int]))
  }
  /** Table description of table dir_mapping. Objects of this class serve as prototypes for rows in queries. */
  class DirMapping(_tableTag: Tag) extends profile.api.Table[DirMappingRow](_tableTag, Some("desu_video"), "dir_mapping") {
    def * = (id, filePath, parentId).<>(DirMappingRow.tupled, DirMappingRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(filePath), Rep.Some(parentId))).shaped.<>({r=>import r._; _1.map(_=> DirMappingRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT UNSIGNED), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column file_path SqlType(VARCHAR), Length(1000,true) */
    val filePath: Rep[String] = column[String]("file_path", O.Length(1000,varying=true))
    /** Database column parent_id SqlType(INT) */
    val parentId: Rep[Int] = column[Int]("parent_id")

    /** Uniqueness Index over (filePath) (database name file_path) */
    val index1 = index("file_path", filePath, unique=true)
  }
  /** Collection-like TableQuery object for table DirMapping */
  lazy val DirMapping = new TableQuery(tag => new DirMapping(tag))
}
