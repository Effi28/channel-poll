import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.paint.Color._


object ChannelPollUI extends JFXApp {
  stage = new PrimaryStage {
    title = "Channel Poll"
    height = 600
    width = 450
    scene = new Scene {
      fill = Black
    }
  }
}