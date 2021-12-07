package gd.robot.akka.ui

import gd.robot.akka.gdactor.gohome.WebAppListener
import gd.robot.akka.mainapp.MainApp
import javafx.scene.input.KeyCode
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.beans.property.BooleanProperty
import scalafx.scene.Scene
import scalafx.scene.control.{Button, TextField}
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color._
import scalafx.stage.Screen

object HelloStageDemo extends JFXApp3 {

  implicit val system           = MainApp.system
  implicit val executionContext = system.executionContext
  val webappListener            = system.systemActorOf(WebAppListener(), "web-app-listener")
  webappListener ! WebAppListener.StartGoHomeKeyListener

  override def start(): Unit = {
    val screenBounds = Screen.primary.bounds
    val stageHelght  = 450

    stage = new JFXApp3.PrimaryStage {
      title.value = "恐怖黎明"
      width = (screenBounds.width - 1440) / 2
      x = screenBounds.width / 2 + 1440 / 2
      y = screenBounds.height / 2 - stageHelght / 2
      height = stageHelght
      scene = new Scene {
        fill = LightBlue

        val button1 = MainApp.delayBuff("name1")
        val button2 = MainApp.delayBuff("name2")

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
            new TextField {
              text.onChange((s1, s2, s3) => button1.delayTime.value = s3.toDouble)
              text = "24"
            },
            new Button {
              text <== when(button1.isOn) choose "关闭第一个 buff 监控" otherwise "启动第一个 buff 监控"
              onMouseClicked = { e =>
                button1.keyCodePro.value = KeyCode.DIGIT6
                button1.isOn.value = !button1.isOn.value
                button1.tick()
              }
            },
            new TextField {
              text.onChange((s1, s2, s3) => button2.delayTime.value = s3.toDouble)
              text = "31"
            },
            new Button {
              text <== when(button2.isOn) choose "关闭第二个 buff 监控" otherwise "启动第二个 buff 监控"
              onMouseClicked = { e =>
                button2.keyCodePro.value = KeyCode.DIGIT7
                button2.isOn.value = !button2.isOn.value
                button2.tick()
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
