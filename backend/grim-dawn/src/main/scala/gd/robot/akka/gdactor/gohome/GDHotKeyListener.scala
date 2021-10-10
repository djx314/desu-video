package gd.robot.akka.gdactor.gohome

import akka.actor.typed.ActorRef
import com.melloware.jintellitype._

import java.awt.event.KeyEvent

class GDHotKeyListener(actorRef: ActorRef[WebAppListener.GoHomeKey]) extends HotkeyListener {
  import GDHotKeyListener._

  override def onHotKey(identifier: Int): Unit = identifier match {
    case STARTKEY1 =>
      actorRef ! WebAppListener.PressGoHomeKeyBoard
    case STARTKEY2 =>
      actorRef ! WebAppListener.PressEnableBuffBoard
    case STARTKEY3 =>
      actorRef ! WebAppListener.RoundAction
    case STOPSYSTEM =>
      actorRef ! WebAppListener.StopWebSystem
      println("System terminate")
    case s => println("监听按键：", s)
  }

}

object GDHotKeyListener {
  val STOPSYSTEM = 88
  val STARTKEY1  = 89
  val STARTKEY2  = 90
  val STARTKEY3  = 91

  def startListen(actorRef: ActorRef[WebAppListener.GoHomeKey]): Unit = {
    val instance = new GDHotKeyListener(actorRef)
    JIntellitype.getInstance().registerHotKey(STARTKEY1, 0, KeyEvent.VK_NUMPAD1)
    JIntellitype.getInstance().registerHotKey(STARTKEY2, 0, KeyEvent.VK_NUMPAD2)
    JIntellitype.getInstance().registerHotKey(STARTKEY3, 0, KeyEvent.VK_NUMPAD3)
    JIntellitype.getInstance().registerHotKey(STOPSYSTEM, 0, KeyEvent.VK_NUMPAD8)
    JIntellitype.getInstance().addHotKeyListener(instance)
  }

  def stopListen: Unit = {
    JIntellitype.getInstance().unregisterHotKey(STARTKEY1)
    JIntellitype.getInstance().unregisterHotKey(STARTKEY2)
    JIntellitype.getInstance().unregisterHotKey(STARTKEY3)
    JIntellitype.getInstance().unregisterHotKey(STOPSYSTEM)
    JIntellitype.getInstance().cleanUp()
  }
}