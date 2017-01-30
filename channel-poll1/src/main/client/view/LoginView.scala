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
    height = 300
    width  = 300
    scene = new Scene {
      val border = new BorderPane()


      val loginButton = new Button("Login")

      val twitterLogin = new TwitterLogin()

      loginButton.onAction = e => {


        val loginURL = twitterLogin.startLogin()
        println(loginURL)
      }

      val loginCode = new TextField()
      loginCode.promptText="Enter your code"
      val submitButton = new Button("Submit")

      submitButton.onAction = e => {
        println(twitterLogin.doLogin(loginCode.getText))
      }

      border.center = new VBox(loginButton, loginCode, submitButton)

      root = border
    }



  }


}



