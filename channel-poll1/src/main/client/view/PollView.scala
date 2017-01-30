package main.client.view

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, TextField, TextInputDialog}
import scalafx.scene.layout.BorderPane

/**
  * Created by Brenda on 30.01.17.
  */
object PollView extends JFXApp{

  stage = new PrimaryStage{
    title = "Create a new poll"
    height = 400
    width = 400
    scene = new Scene(){
      val border = BorderPane()
      val questionLabel = new Label("Question:")
      val questionTextField = new TextField()
      questionTextField.promptText = "Ask something..."






      val addOptionButton = new Button("Add Option")

      addOptionButton.onAction = e => {
        val dialog = new TextInputDialog(){
          initOwner(stage)
          title = "Add Option"
          headerText = "Add Option"
          contentText = "Add Option..."
        }
      }

      val createPollButton = new Button("Create Poll")

      root = border
    }
  }

}
