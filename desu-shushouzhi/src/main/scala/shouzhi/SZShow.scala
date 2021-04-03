package shouzhi

trait SZShow[T] {
  def toList(list: T): List[String] => List[String]
}

object SZShow {
  implicit val intSZShow: SZShow[Int] = new SZShow[Int] {
    override def toList(list: Int): List[String] => List[String] = l => list.toString :: l
  }

  def show[T](t: T)(implicit i: SZShow[T]) = i.toList(t)(List.empty).mkString("(", ", ", ")")
}
