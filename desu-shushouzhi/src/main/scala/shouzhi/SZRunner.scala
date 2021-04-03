package shouzhi

object SZRunner {

  def main(arr: Array[String]): Unit = {
    println(SZShow.show(SZTuple(1)))
    println(SZShow.show(SZTuple(1, 2)))
    println(SZShow.show(SZTuple(1, 2, 3)))

  }

}
