package gd.robot.akka.ui

import gd.robot.akka.gdactor.gohome.WebAppListener
import gd.robot.akka.mainapp.MainApp
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.beans.property.BooleanProperty
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox
import scalafx.scene.paint.Color._
import scalafx.stage.Screen

object HelloStageDemo extends JFXApp3 {

  implicit val system           = MainApp.system
  implicit val executionContext = system.executionContext
  val webappListener            = MainApp.webappListener
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

        val showVBox = new VBox {
          children = List(
            new Button("回城") {
              onMouseClicked = { e =>
                webappListener ! WebAppListener.PressGoHomeKeyBoard
              }
            },
            /*new Button {
              val isOn = BooleanProperty(false)
              text <== when(isOn) choose "关闭重生之语" otherwise "启动重生之语"

              onMouseClicked = { e =>
                isOn.value = !isOn.value
                webappListener ! WebAppListener.RoundAction
              }
            },*/
            new Button {
              val isOn = BooleanProperty(false)
              text <== when(isOn) choose "关闭 buff 监控" otherwise "启动 buff 监控"

              onMouseClicked = { e =>
                isOn.value = !isOn.value
                webappListener ! WebAppListener.PressAutoEnableBuffBoard
              }
            },
            MainApp.delayBuffUI.ui,
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
