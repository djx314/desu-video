package shouzhi

trait SZList
case class SZPositive[H <: SZList: SZShow, T: SZShow](head: T, next: H) extends SZList
object SZPositive {
  implicit def snilImplicit[H <: SZList: SZShow, T: SZShow]: SZShow[SZPositive[H, T]] = new SZShow[SZPositive[H, T]] {
    override def toList(list: SZPositive[H, T]): List[String] => List[String] = l =>
      implicitly[SZShow[T]].toList(list.head)(implicitly[SZShow[H]].toList(list.next)(l))
  }
}
class SZNil extends SZList
object SZNil {
  val value: SZNil = new SZNil
  implicit val snilImplicit: SZShow[SZNil] = new SZShow[SZNil] {
    override def toList(list: SZNil): List[String] => List[String] = l => l
  }
}
