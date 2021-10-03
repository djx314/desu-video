package gd.robot.akka.gdactor.gohome

import akka.actor.typed.ActorRef
import com.melloware.jintellitype._

import java.awt.event.KeyEvent

class GDHotKeyListener(actorRef: ActorRef[GoHomeKeyListener.GoHomeKey]) extends HotkeyListener {
  val STARTKEY   = 89
  val STOPSYSTEM = 90

  override def onHotKey(identifier: Int): Unit = identifier match {
    case STARTKEY =>
      actorRef ! GoHomeKeyListener.PressGoHomeKeyBoard
    case STOPSYSTEM =>
      actorRef ! GoHomeKeyListener.StopWebSystem
      println("System terminate")
    case s => println("监听按键：", s)
  }

  def startListen: Unit = {
    JIntellitype.getInstance().registerHotKey(STARTKEY, 0, KeyEvent.VK_F11)
    JIntellitype.getInstance().registerHotKey(STOPSYSTEM, 0, KeyEvent.VK_F10)
    JIntellitype.getInstance().addHotKeyListener(this)
    actorRef ! GoHomeKeyListener.ReadyToListen
  }

  def stopListen: Unit = {
    JIntellitype.getInstance().unregisterHotKey(STARTKEY)
    JIntellitype.getInstance().unregisterHotKey(STOPSYSTEM)
    JIntellitype.getInstance().cleanUp()
  }
}
