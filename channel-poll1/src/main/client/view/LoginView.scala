/**
  * Created by Brenda on 30.01.17.
  */
package main.client.view

import main.client.TwitterLogin

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.scene.layout.{BorderPane, VBox}

/**
  * Created by Brenda on 30.01.17.
  */

object LoginView extends JFXApp {


  stage = new PrimaryStage {
    title = "Login"
    height = 500
    width  = 500
    scene = new Scene {
      val border = new BorderPane()


      val loginButton = new Button("Login")

      loginButton.onAction = e => {

        val twitterLogin = new TwitterLogin
        twitterLogin.login()

      }

      val loginCode = new TextField()
      loginCode.promptText="Enter your code"


      border.center = loginButton

      root = border
    }



  }


}



