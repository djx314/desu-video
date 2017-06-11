package net.scalax.mp4.model

case class DateInfo(year: Int, month: Int, day: Int) {
  val actualMonth = if (month < 10)
    s"0$month"
  else
    month.toString

  val actualDay = if (day < 10)
    s"0$day"
  else
    day.toString

  def toYearMonth: String = {
    s"$year$actualMonth"
  }
  def toYearMonthDay: String = {
    s"$year$actualMonth$actualDay"
  }
  /*def toEncodeRequest(prefix: String): EncodeRequest = {
    val actualMonth = if (month < 10)
      s"0$month"
    else
      month.toString

    val actualDay = if (day < 10)
      s"0$day"
    else
      day.toString

    val patiSuffix = s"${year}${actualMonth}/${year}${actualMonth}${actualDay}.mp4"
    EncodeRequest(s"${prefix}source/$patiSuffix", this)
  }*/
}

/*case class EncodeRequest(sourcePath: String, targetDate: DateInfo)*/

case class RequestInfo(isSuccessed: Boolean, message: String)