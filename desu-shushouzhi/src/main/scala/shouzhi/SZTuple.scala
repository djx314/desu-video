package shouzhi

class SZTuple1[T1](val i1: T1)
object SZTuple1 {
  implicit def szTuple1SZShow[T1](implicit implicit1: SZShow[T1]): SZShow[SZTuple1[T1]] = new SZShow[SZTuple1[T1]] {
    override def toList(list: SZTuple1[T1]): List[String] => List[String] = l => implicit1.toList(list.i1)(l)
  }
}

class SZTuple2[T1, T2](val i1: T1, val i2: T2)
object SZTuple2 {
  implicit def szTuple2SZShow[T1, T2](implicit implicit1: SZShow[T1], implicit2: SZShow[T2]): SZShow[SZTuple2[T1, T2]] =
    new SZShow[SZTuple2[T1, T2]] {
      override def toList(list: SZTuple2[T1, T2]): List[String] => List[String] = l =>
        implicit1.toList(list.i1)(implicit2.toList(list.i2)(l))
    }
}

class SZTuple3[T1, T2, T3](val i1: T1, val i2: T2, val i3: T3)
object SZTuple3 {
  implicit def szTuple3SZShow[T1, T2, T3](implicit
    implicit1: SZShow[T1],
    implicit2: SZShow[T2],
    implicit3: SZShow[T3]
  ): SZShow[SZTuple3[T1, T2, T3]] = new SZShow[SZTuple3[T1, T2, T3]] {
    override def toList(list: SZTuple3[T1, T2, T3]): List[String] => List[String] = l =>
      implicit1.toList(list.i1)(implicit2.toList(list.i2)(implicit3.toList(list.i3)(l)))
  }
}

class SZTuple4[T1, T2, T3, T4](val i1: T1, val i2: T2, val i3: T3, val i4: T4)
object SZTuple4 {
  implicit def szTuple4SZShow[T1, T2, T3, T4](implicit
    implicit1: SZShow[T1],
    implicit2: SZShow[T2],
    implicit3: SZShow[T3],
    implicit4: SZShow[T4]
  ): SZShow[SZTuple4[T1, T2, T3, T4]] = new SZShow[SZTuple4[T1, T2, T3, T4]] {
    override def toList(list: SZTuple4[T1, T2, T3, T4]): List[String] => List[String] = l =>
      implicit1.toList(list.i1)(implicit2.toList(list.i2)(implicit3.toList(list.i3)(implicit4.toList(list.i4)(l))))
  }
}

class SZTuple5[T1, T2, T3, T4, T5](val i1: T1, val i2: T2, val i3: T3, val i4: T4, val i5: T5)
object SZTuple5 {
  implicit def szTuple5SZShow[T1, T2, T3, T4, T5](implicit
    implicit1: SZShow[T1],
    implicit2: SZShow[T2],
    implicit3: SZShow[T3],
    implicit4: SZShow[T4],
    implicit5: SZShow[T5]
  ): SZShow[SZTuple5[T1, T2, T3, T4, T5]] = new SZShow[SZTuple5[T1, T2, T3, T4, T5]] {
    override def toList(list: SZTuple5[T1, T2, T3, T4, T5]): List[String] => List[String] = l =>
      implicit1.toList(list.i1)(
        implicit2.toList(list.i2)(implicit3.toList(list.i3)(implicit4.toList(list.i4)(implicit5.toList(list.i5)(l))))
      )
  }
}

class SZTuple6[T1, T2, T3, T4, T5, T6](val i1: T1, val i2: T2, val i3: T3, val i4: T4, val i5: T5, val i6: T6)
object SZTuple6 {
  implicit def szTuple6SZShow[T1, T2, T3, T4, T5, T6](implicit
    implicit1: SZShow[T1],
    implicit2: SZShow[T2],
    implicit3: SZShow[T3],
    implicit4: SZShow[T4],
    implicit5: SZShow[T5],
    implicit6: SZShow[T6]
  ): SZShow[SZTuple6[T1, T2, T3, T4, T5, T6]] = new SZShow[SZTuple6[T1, T2, T3, T4, T5, T6]] {
    override def toList(list: SZTuple6[T1, T2, T3, T4, T5, T6]): List[String] => List[String] = l =>
      implicit1.toList(list.i1)(
        implicit2.toList(list.i2)(
          implicit3.toList(list.i3)(implicit4.toList(list.i4)(implicit5.toList(list.i5)(implicit6.toList(list.i6)(l))))
        )
      )
  }
}

object SZTuple {
  def apply[T1](i1: T1): SZTuple1[T1]                                                 = new SZTuple1(i1 = i1)
  def apply[T1, T2](i1: T1, i2: T2): SZTuple2[T1, T2]                                 = new SZTuple2(i1 = i1, i2 = i2)
  def apply[T1, T2, T3](i1: T1, i2: T2, i3: T3): SZTuple3[T1, T2, T3]                 = new SZTuple3(i1 = i1, i2 = i2, i3 = i3)
  def apply[T1, T2, T3, T4](i1: T1, i2: T2, i3: T3, i4: T4): SZTuple4[T1, T2, T3, T4] = new SZTuple4(i1 = i1, i2 = i2, i3 = i3, i4 = i4)
  def apply[T1, T2, T3, T4, T5](i1: T1, i2: T2, i3: T3, i4: T4, i5: T5): SZTuple5[T1, T2, T3, T4, T5] =
    new SZTuple5(i1 = i1, i2 = i2, i3 = i3, i4 = i4, i5 = i5)
  def apply[T1, T2, T3, T4, T5, T6](i1: T1, i2: T2, i3: T3, i4: T4, i5: T5, i6: T6): SZTuple6[T1, T2, T3, T4, T5, T6] =
    new SZTuple6(i1 = i1, i2 = i2, i3 = i3, i4 = i4, i5 = i5, i6 = i6)
}
