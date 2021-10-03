package gd.robot.akka.gdactor.gohome

import akka.actor.typed.ActorRef
import com.melloware.jintellitype._

import java.awt.event.KeyEvent

class GDHotKeyListener(actorRef: ActorRef[WebAppListener.GoHomeKey]) extends HotkeyListener {
  import GDHotKeyListener._

  override def onHotKey(identifier: Int): Unit = identifier match {
    case STARTKEY =>
      actorRef ! WebAppListener.PressGoHomeKeyBoard
    case STOPSYSTEM =>
      actorRef ! WebAppListener.StopWebSystem
      println("System terminate")
    case s => println("监听按键：", s)
  }

  def startListen: Unit = {
    JIntellitype.getInstance().registerHotKey(STARTKEY, 0, KeyEvent.VK_F11)
    JIntellitype.getInstance().registerHotKey(STOPSYSTEM, 0, KeyEvent.VK_F10)
    JIntellitype.getInstance().addHotKeyListener(this)
    actorRef ! WebAppListener.ReadyToListen
  }

}

object GDHotKeyListener {
  val STARTKEY   = 89
  val STOPSYSTEM = 90
  def stopListen: Unit = {
    JIntellitype.getInstance().unregisterHotKey(STARTKEY)
    JIntellitype.getInstance().unregisterHotKey(STOPSYSTEM)
    JIntellitype.getInstance().cleanUp()
  }
}
