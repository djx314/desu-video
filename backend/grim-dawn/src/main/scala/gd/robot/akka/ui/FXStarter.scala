package gd.robot.akka.ui

import gd.robot.akka.gdactor.gohome.WebAppListener
import gd.robot.akka.mainapp.MainApp
import javafx.scene.input.KeyCode
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.beans.property.{BooleanProperty, BufferProperty}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.scene.layout.{Border, BorderStroke, BorderStrokeStyle, BorderWidths, CornerRadii, HBox, Pane, VBox}
import scalafx.scene.paint.Color._
import scalafx.scene.paint.Paint
import scalafx.stage.Screen

object HelloStageDemo extends JFXApp3 {

  implicit val system           = MainApp.system
  implicit val executionContext = system.executionContext
  val webappListener            = system.systemActorOf(WebAppListener(), "web-app-listener")
  webappListener ! WebAppListener.StartGoHomeKeyListener

  override def start(): Unit = {
    val screenBounds = Screen.primary.bounds
    val stageHelght  = 800

    stage = new JFXApp3.PrimaryStage {
      title.value = "恐怖黎明"
      width = (screenBounds.width - 1440) / 2
      x = screenBounds.width / 2 + 1440 / 2
      y = screenBounds.height / 2 - stageHelght / 2
      height = stageHelght
      scene = new Scene {
        fill = LightBlue

        val button1 = MainApp.delayBuff("name1")
        button1.delayTime.value = 24
        button1.keyCodePro.value = KeyCode.DIGIT6
        val button2 = MainApp.delayBuff("name2")
        button2.delayTime.value = 31
        button2.keyCodePro.value = KeyCode.DIGIT7
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
              new TextField {
                text.onChange((s1, s2, s3) => delayBuff.delayTime.value = s3.toDouble)
                text = delayBuff.delayTime.value.toString
              },
              new Button {
                text <== when(delayBuff.isOn) choose s"关闭第${index + 1}个 buff 监控" otherwise s"启动第${index + 1}个 buff 监控"
                onMouseClicked = { e =>
                  delayBuff.keyCodePro.value = KeyCode.DIGIT7
                  delayBuff.isOn.value = !button2.isOn.value
                  delayBuff.tick()
                }
              },
              new Button("删除") {
                onMouseClicked = { e =>
                  bufferBind.remove(delayBuff)
                }
              }
            )
          }
        }

        val showVBox = new VBox {
          children = List(
            new Button("回城") {
              onMouseClicked = { e =>
                webappListener ! WebAppListener.PressGoHomeKeyBoard
              }
            },
            new Button {
              val isOn = BooleanProperty(false)
              text <== when(isOn) choose "关闭重生之语" otherwise "启动重生之语"

              onMouseClicked = { e =>
                isOn.value = !isOn.value
                webappListener ! WebAppListener.RoundAction
              }
            },
            new Button {
              val isOn = BooleanProperty(false)
              text <== when(isOn) choose "关闭 buff 监控" otherwise "启动 buff 监控"

              onMouseClicked = { e =>
                isOn.value = !isOn.value
                webappListener ! WebAppListener.PressAutoEnableBuffBoard
              }
            },
            new VBox {
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
                        val buttonRandom = MainApp.delayBuff(s"name${util.Random.nextInt()}")
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
                  bufferBind.value = ObservableBuffer(button1, button2)
                }
              )
            },
            new Button("关闭监听") {
              onMouseClicked = { e =>
                stage.close()
              }
            }
          )
        }

        content = showVBox
      }
    }
  }

  override def stopApp(): Unit = {
    webappListener ! WebAppListener.StopWebSystem
  }
}
