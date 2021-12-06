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

        val startButton = new Button("启动监听") {
          onMouseClicked = { e =>
            webappListener ! WebAppListener.StartGoHomeKeyListener
            innerVBox.children = stopButton
          }
        }

        val stopButton = new Button("关闭监听") {
          onMouseClicked = { e =>
            stage.close()
          }
        }

        val innerVBox = new VBox

        content = new VBox {
          children = List(
            startButton,
            innerVBox
          )
          fill = Red
        }
      }
    }
  }

  override def stopApp(): Unit = {
    webappListener ! WebAppListener.StopWebSystem
  }
}
