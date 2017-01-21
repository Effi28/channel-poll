import scalafx.scene.control._

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout._
import scalafx.scene.paint.Color._


object ChannelPollUI extends JFXApp {
  stage = new PrimaryStage {
    title = "Channel Poll"
    height = 600
    width = 500
    scene = new Scene {
      val border = new BorderPane()

      val statementBox1 = new VBox(new TextArea("Statement 1"), new HBox(new Button("Like"), new Button("Comment"), new Button("Poll")))
      val statementBox2 = new VBox(new TextArea("Statement 2"), new HBox(new Button("Like"), new Button("Comment"), new Button("Poll")))
      val statementBox3 = new VBox(new TextArea("Statement 3"), new HBox(new Button("Like"), new Button("Comment"), new Button("Poll")))

      val statements = List("statement 1", "statement 2", "statement 3")
      val newStatement = "statement 4"

      //statements + newStatement   //--> wie fügt man neue Elemente hinzu?


      border.top = new Label("ChannelPoll")
      border.bottom = new Button("Logout")
      //border.left
      //border.right
      border.center = new VBox(statementBox1, statementBox2, statementBox3)

      root = border


    }
  }
}