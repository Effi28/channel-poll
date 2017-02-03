package main.client.view

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, HBox, VBox}

/**
  * Created by Brenda on 30.01.17.
  */
class PollView extends JFXApp{

  def start() {

    stage = new PrimaryStage {
      title = "Create a new poll"
      height = 400
      width = 400
      scene = new Scene() {
        val border = new BorderPane()
        val questionLabel = new Label("Question:")
        val questionTextField = new TextField()
        questionTextField.promptText = "Ask something..."


        val options = new VBox()

        val addOptionButton = new Button("Add Option")

        addOptionButton.onAction = e => {
          val dialog = new TextInputDialog() {
            initOwner(stage)
            title = "Add Option"
            headerText = "Add Option"
            contentText = "Add Option..."
          }

          val result = dialog.showAndWait()

          result match {
            case Some(option) => {

              val radioButton = new RadioButton(option)
              options.children.add(radioButton)
              println("Your option: " + option)


            }


            case None => println("Dialog was canceled.")
          }
        }


        val createPollButton = new Button("Create Poll")

        val vbox = new VBox(new HBox(questionLabel, questionTextField), addOptionButton)

        border.center = vbox
        border.bottom = createPollButton

        root = border
      }
    }


  }


}
