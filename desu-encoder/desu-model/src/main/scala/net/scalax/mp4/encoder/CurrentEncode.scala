package net.scalax.mp4.encoder

trait CurrentEncode {

  protected val currentEncodeVideos: scala.collection.mutable.ListBuffer[String] = scala.collection.mutable.ListBuffer.empty[String]

  def addVideoKey(uuid: String): Unit    = currentEncodeVideos.append(uuid)
  def removeVideoKey(uuid: String): Unit = currentEncodeVideos.remove { currentEncodeVideos.indexWhere(s => s == uuid) }
  def keyExists(uuid: String): Boolean   = currentEncodeVideos.exists(s => s == uuid)

}
