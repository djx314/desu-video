package gd.robot.akka.gdactor.gohome

import akka.actor.typed.ActorRef
import com.melloware.jintellitype._

import java.awt.event.KeyEvent

class GDHotKeyListener(actorRef: ActorRef[WebAppListener.GoHomeKey]) extends HotkeyListener {
  import GDHotKeyListener._

  override def onHotKey(identifier: Int): Unit = identifier match {
    case STARTKEY5 =>
      actorRef ! WebAppListener.PressSkillRound
    case s => println("监听按键：", s)
  }

}

object GDHotKeyListener extends AutoCloseable {
  val STARTKEY5 = 93

  def startListen(actorRef: ActorRef[WebAppListener.GoHomeKey]): Unit = {
    val instance = new GDHotKeyListener(actorRef)
    JIntellitype.getInstance().registerHotKey(STARTKEY5, 0, KeyEvent.VK_BACK_QUOTE)
    JIntellitype.getInstance().addHotKeyListener(instance)
  }

  override def close(): Unit = {
    JIntellitype.getInstance().unregisterHotKey(STARTKEY5)
    JIntellitype.getInstance().cleanUp()
  }
}
