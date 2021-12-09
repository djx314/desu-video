package gd.robot.akka.ui

import javafx.scene.input.KeyCode
import scalafx.Includes._
import scalafx.beans.property.BufferProperty
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.scene.layout.{Border, BorderStroke, BorderStrokeStyle, BorderWidths, CornerRadii, HBox, VBox}
import scalafx.scene.paint.Paint

class DelayBuffUI(gen: () => DelayBuff) {

  lazy val 战争领主Buff监控 = {
    val buff1 = gen()
    buff1.delayTime.value = 25
    buff1.keyCodePro.value = KeyCode.DIGIT6
    val buff2 = gen()
    buff2.delayTime.value = 25
    buff2.keyCodePro.value = KeyCode.DIGIT7
    List(buff1, buff2)
  }
  lazy val 净化Buff监控 = {
    val buff1 = gen()
    buff1.delayTime.value = 15
    buff1.keyCodePro.value = KeyCode.DIGIT6
    val buff2 = gen()
    buff2.delayTime.value = 27
    buff2.keyCodePro.value = KeyCode.R
    List(buff1, buff2)
  }
  val initBuff = 战争领主Buff监控

  val bufferBind = BufferProperty(List.empty[DelayBuff])
  bufferBind.onChange { (bind, old, newInstance) =>
    val needClose = old.filter(s => !newInstance.exists(t => s == t))
    needClose.foreach(_.close())
  }
  def toVBox(delayBuff: DelayBuff, index: Int): VBox = {
    new VBox {
      margin = Insets(8, 0, 5, 0)
      padding = Insets(8, 0, 0, 2)
      val paint = Paint.valueOf("rgb(0,0,0)")
      border = new Border(
        new BorderStroke(
          paint,
          paint,
          paint,
          paint,
          BorderStrokeStyle.Dashed,
          BorderStrokeStyle.None,
          BorderStrokeStyle.None,
          BorderStrokeStyle.None,
          radii = CornerRadii.Empty,
          widths = BorderWidths.Default,
          insets = Insets.Empty
        )
      )
      children = List(
        new HBox {
          margin = Insets(2, 0, 2, 0)
          alignment = Pos.CenterLeft
          children = List(
            new Label("时间间隔"),
            new TextField {
              prefWidth = 100
              margin = Insets(0, 5, 0, 5)
              text.onChange((s1, s2, s3) => delayBuff.delayTime.value = s3.toDouble)
              text = delayBuff.delayTime.value.toString
            },
            new Label("秒")
          )
        },
        new HBox {
          margin = Insets(2, 0, 2, 0)
          alignment = Pos.CenterLeft
          children = List(
            new Label("按键"),
            new TextField {
              margin = Insets(0, 0, 0, 5)
              text = delayBuff.keyCodePro.value.getName
            }
          )
        },
        new Button {
          margin = Insets(2, 0, 2, 0)
          text <== when(delayBuff.isOn) choose s"关闭第${index + 1}个 buff 监控" otherwise s"启动第${index + 1}个 buff 监控"
          onMouseClicked = { e =>
            delayBuff.isOn.value = !delayBuff.isOn.value
            delayBuff.tick()
          }
        },
        new Button("删除") {
          margin = Insets(2, 0, 2, 0)
          onMouseClicked = { e =>
            bufferBind.remove(delayBuff)
          }
        }
      )
    }
  }

  val ui = new VBox {
    margin = Insets(8, 0, 5, 2)
    padding = Insets(8, 0, 0, 4)
    val paint = Paint.valueOf("rgb(0,0,0)")
    border = new Border(
      new BorderStroke(
        paint,
        paint,
        paint,
        paint,
        BorderStrokeStyle.Solid,
        BorderStrokeStyle.None,
        BorderStrokeStyle.None,
        BorderStrokeStyle.None,
        radii = CornerRadii.Empty,
        widths = BorderWidths.Default,
        insets = Insets.Empty
      )
    )
    children = List(
      new HBox {
        alignment = Pos.CenterLeft
        children = List(
          new Button("+") {
            onMouseClicked = { e =>
              val buttonRandom = gen()
              buttonRandom.delayTime.value = 31
              bufferBind.value = bufferBind.value.appended(buttonRandom)
            }
          },
          new Label("增加监听") {
            margin = Insets(0, 0, 0, 5)
          }
        )
      },
      new VBox {
        bufferBind.onChange((bind, old, newInstance) => children = newInstance.zipWithIndex.map(s => toVBox(s._1, s._2)))
        bufferBind.value = ObservableBuffer(initBuff: _*)
      }
    )
  }

}
