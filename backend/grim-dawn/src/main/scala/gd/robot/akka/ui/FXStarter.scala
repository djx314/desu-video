package gd.robot.akka.ui

import gd.robot.akka.gdactor.gohome.WebAppListener
import gd.robot.akka.mainapp.MainApp
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.Button
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
      title.value = "Hello Stage"
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
            new Button("启动或关闭重生之语") {
              onMouseClicked = { e =>
                webappListener ! WebAppListener.RoundAction
              }
            },
            new Button("启动或关闭 buff 监控") {
              onMouseClicked = { e =>
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
