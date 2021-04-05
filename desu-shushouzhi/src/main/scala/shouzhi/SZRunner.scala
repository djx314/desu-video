package shouzhi

object SZRunner {

  def main(arr: Array[String]): Unit = {
    println(SZShow.show(SZTuple(1)))
    println(SZShow.show(SZTuple(1, 2)))
    println(SZShow.show(SZTuple(1, 2, 3)))

    {
      val value1 = SZPositive(
        SZTuple(1, 2),
        SZPositive(
          SZTuple(SZTuple(3, 4, 5), SZTuple(6, 7, 8)),
          SZPositive(
            SZTuple(
              SZTuple(SZTuple(9, 10, 11), SZTuple(12, 13, 14), SZTuple(15, 16, 17)),
              SZTuple(SZTuple(18, 19, 20), SZTuple(21, 22, 23), SZTuple(24, 25, 26))
            ),
            SZNil.value
          )
        )
      )

      println(SZShow.show(value1))
      println(value1.head.i1)
      println(value1.head.i2)
      println(value1.next.head.i1.i1)
      println(value1.next.head.i1.i2)
      println(value1.next.head.i1.i3)
      println(value1.next.head.i2.i1)
      println(value1.next.head.i2.i2)
      println(value1.next.head.i2.i3)
      println(value1.next.next.head.i1.i1.i1)
      println(value1.next.next.head.i1.i1.i2)
      println(value1.next.next.head.i1.i1.i3)
      println(value1.next.next.head.i1.i2.i1)
      println(value1.next.next.head.i1.i2.i2)
      println(value1.next.next.head.i1.i2.i3)
      println(value1.next.next.head.i1.i3.i1)
      println(value1.next.next.head.i1.i3.i2)
      println(value1.next.next.head.i1.i3.i3)
      println(value1.next.next.head.i2.i1.i1)
      println(value1.next.next.head.i2.i1.i2)
      println(value1.next.next.head.i2.i1.i3)
      println(value1.next.next.head.i2.i2.i1)
      println(value1.next.next.head.i2.i2.i2)
      println(value1.next.next.head.i2.i2.i3)
      println(value1.next.next.head.i2.i3.i1)
      println(value1.next.next.head.i2.i3.i2)
      println(value1.next.next.head.i2.i3.i3)
    }
  }

}
