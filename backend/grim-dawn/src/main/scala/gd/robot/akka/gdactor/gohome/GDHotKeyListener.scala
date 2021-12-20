package gd.robot.akka.gdactor.gohome

import akka.actor.typed.ActorRef
import com.melloware.jintellitype._
import zio._

import java.awt.event.KeyEvent

class GDHotKeyListener(actorRef: ActorRef[WebAppListener.GoHomeKey]) extends HotkeyListener {
  import GDHotKeyListener._

  override def onHotKey(identifier: Int): Unit = identifier match {
    case STARTKEY5 =>
      actorRef ! WebAppListener.PressSkillRound
    case s => println("监听按键：", s)
  }

}

object GDHotKeyListener {
  val STARTKEY5 = 93

  def startListen(actorRef: ActorRef[WebAppListener.GoHomeKey]): ZManaged[blocking.Blocking, Throwable, Unit] = {
    val addHotKey1     = blocking.effectBlocking(JIntellitype.getInstance().registerHotKey(STARTKEY5, 0, KeyEvent.VK_BACK_QUOTE))
    val releaseHotKey1 = blocking.effectBlocking(JIntellitype.getInstance().unregisterHotKey(STARTKEY5)).option
    val managed1       = ZManaged.make_(addHotKey1)(releaseHotKey1)

    val addListener     = blocking.effectBlocking(JIntellitype.getInstance().addHotKeyListener(new GDHotKeyListener(actorRef)))
    val releaseListener = blocking.effectBlocking(JIntellitype.getInstance().cleanUp()).option
    for {
      _ <- managed1
      _ <- ZManaged.make_(addListener)(releaseListener)
    } yield {}
  }

}
