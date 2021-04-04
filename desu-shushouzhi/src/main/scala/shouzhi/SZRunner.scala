package shouzhi

object SZRunner {

  def main(arr: Array[String]): Unit = {
    println(SZShow.show(SZTuple(1)))
    println(SZShow.show(SZTuple(1, 2)))
    println(SZShow.show(SZTuple(1, 2, 3)))

    /*{
      val value1 = SZPositive(SZTuple(1, 2, 3), SZPositive(SZTuple(SZPositive(SZTuple(4), SZNil.value)), SZNil.value))
      println(SZShow.show(value1))
      println(value1.head.i1)
      println(value1.head.i2)
      println(value1.head.i3)
      println(value1.next.head.i1.head.i1)
    }

    {
      val value1 = SZPositive(SZTuple(1, 2, 3), SZPositive(SZTuple(SZPositive(SZTuple(4, 5), SZNil.value)), SZNil.value))
      println(SZShow.show(value1))
      println(value1.head.i1)
      println(value1.head.i2)
      println(value1.head.i3)
      println(value1.next.head.i1.head.i1)
      println(value1.next.head.i1.head.i2)
    }

    {
      val value1 = SZPositive(SZTuple(1, 2, 3), SZPositive(SZTuple(SZPositive(SZTuple(4, 5, 6), SZNil.value)), SZNil.value))
      println(SZShow.show(value1))
      println(value1.head.i1)
      println(value1.head.i2)
      println(value1.head.i3)
      println(value1.next.head.i1.head.i1)
      println(value1.next.head.i1.head.i2)
      println(value1.next.head.i1.head.i3)
    }

    {
      val value1 = SZPositive(
        SZTuple(1, 2, 3),
        SZPositive(SZTuple(SZPositive(SZTuple(4, 5, 6), SZNil.value), SZPositive(SZTuple(7), SZNil.value)), SZNil.value)
      )
      println(SZShow.show(value1))
      println(value1.head.i1)
      println(value1.head.i2)
      println(value1.head.i3)
      println(value1.next.head.i1.head.i1)
      println(value1.next.head.i1.head.i2)
      println(value1.next.head.i1.head.i3)
      println(value1.next.head.i2.head.i1)
    }

    {
      val value1 = SZPositive(
        SZTuple(1, 2, 3),
        SZPositive(SZTuple(SZPositive(SZTuple(4, 5, 6), SZNil.value), SZPositive(SZTuple(7, 8), SZNil.value)), SZNil.value)
      )
      println(SZShow.show(value1))
      println(value1.head.i1)
      println(value1.head.i2)
      println(value1.head.i3)
      println(value1.next.head.i1.head.i1)
      println(value1.next.head.i1.head.i2)
      println(value1.next.head.i1.head.i3)
      println(value1.next.head.i2.head.i1)
      println(value1.next.head.i2.head.i2)
    }

    {
      val value1 = SZPositive(
        SZTuple(1, 2, 3),
        SZPositive(
          SZTuple(
            SZPositive(SZTuple(4, 5, 6), SZNil.value),
            SZPositive(SZTuple(7, 8, 9), SZNil.value),
            SZPositive(SZTuple(10, 11, 12), SZNil.value)
          ),
          SZNil.value
        )
      )
      println(SZShow.show(value1))
      println(value1.head.i1)
      println(value1.head.i2)
      println(value1.head.i3)
      println(value1.next.head.i1.head.i1)
      println(value1.next.head.i1.head.i2)
      println(value1.next.head.i1.head.i3)
      println(value1.next.head.i2.head.i1)
      println(value1.next.head.i2.head.i2)
      println(value1.next.head.i2.head.i3)
      println(value1.next.head.i3.head.i1)
      println(value1.next.head.i3.head.i2)
      println(value1.next.head.i3.head.i3)
    }

    {
      val value1 = SZPositive(
        SZTuple(1, 2, 3),
        SZPositive(
          SZTuple(
            SZPositive(SZTuple(4, 5, 6), SZNil.value),
            SZPositive(SZTuple(7, 8, 9), SZNil.value),
            SZPositive(SZTuple(10, 11, 12), SZNil.value)
          ),
          SZPositive(
            SZTuple(
              SZPositive(SZTuple(13), SZNil.value)
            ),
            SZNil.value
          )
        )
      )
      println(SZShow.show(value1))
      println(value1.head.i1)
      println(value1.head.i2)
      println(value1.head.i3)
      println(value1.next.head.i1.head.i1)
      println(value1.next.head.i1.head.i2)
      println(value1.next.head.i1.head.i3)
      println(value1.next.head.i2.head.i1)
      println(value1.next.head.i2.head.i2)
      println(value1.next.head.i2.head.i3)
      println(value1.next.head.i3.head.i1)
      println(value1.next.head.i3.head.i2)
      println(value1.next.head.i3.head.i3)
      println(value1.next.next.head.i1.head.i1)
    }

    {
      val value1 = SZPositive(
        SZTuple(1, 2, 3),
        SZPositive(
          SZTuple(
            SZPositive(SZTuple(4, 5, 6), SZNil.value),
            SZPositive(SZTuple(7, 8, 9), SZNil.value),
            SZPositive(SZTuple(10, 11, 12), SZNil.value)
          ),
          SZPositive(
            SZTuple(
              SZPositive(SZNil.value, SZPositive(SZTuple(SZPositive(SZTuple(13), SZNil.value)), SZNil.value))
            ),
            SZNil.value
          )
        )
      )
      println(SZShow.show(value1))
      println(value1.head.i1)
      println(value1.head.i2)
      println(value1.head.i3)
      println(value1.next.head.i1.head.i1)
      println(value1.next.head.i1.head.i2)
      println(value1.next.head.i1.head.i3)
      println(value1.next.head.i2.head.i1)
      println(value1.next.head.i2.head.i2)
      println(value1.next.head.i2.head.i3)
      println(value1.next.head.i3.head.i1)
      println(value1.next.head.i3.head.i2)
      println(value1.next.head.i3.head.i3)
      println(value1.next.next.head.i1.next.head.i1.head.i1)
    }*/

    {
      val value1 = SZPositive(
        SZTuple(1, 2),
        SZPositive(
          SZTuple(
            SZPositive(SZTuple(3, 4, 5), SZNil.value),
            SZPositive(SZTuple(6, 7, 8), SZNil.value)
          ),
          SZPositive(
            SZTuple(
              SZPositive(
                SZTuple(
                  SZPositive(SZTuple(9, 10, 11), SZNil.value),
                  SZPositive(SZTuple(12, 13, 14), SZNil.value),
                  SZPositive(SZTuple(15, 16, 17), SZNil.value)
                ),
                SZNil.value
              ),
              SZPositive(
                SZTuple(
                  SZPositive(SZTuple(18, 19, 20), SZNil.value),
                  SZPositive(SZTuple(21, 22, 23), SZNil.value),
                  SZPositive(SZTuple(24, 25, 26), SZNil.value)
                ),
                SZNil.value
              )
            ),
            SZNil.value
          )
        )
      )

      println(SZShow.show(value1))
      println(value1.next.next.head.i1.head.i1.head.i1)
      println(value1.head.i1)
      println(value1.head.i2)
      println(value1.next.head.i1.head.i2)
      println(value1.next.head.i1.head.i3)
      println(value1.next.head.i2.head.i1)
      println(value1.next.head.i2.head.i2)
      println(value1.next.head.i2.head.i3)
    }
  }

}
