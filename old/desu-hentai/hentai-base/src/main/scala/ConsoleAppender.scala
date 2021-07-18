package logback

import ch.qos.logback.core.encoder.Encoder
import ch.qos.logback.core.{ConsoleAppender => LConsoleAppender}

class ConsoleAppender[E] extends LConsoleAppender[E] {

  override def setEncoder(encoder: Encoder[E]): Unit = {
    this.encoder = encoder
  }

}
