package shouzhi

object SZRunner {

  def main(arr: Array[String]): Unit = {
    println(SZShow.show(SZTuple(1, 2, 3, SZTuple(4, 5, 6, 7, 8, 9), SZTuple(10, 11, 12, 13, 14, 15))))
  }

}
