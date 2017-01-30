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

class LoginView extends JFXApp {


  stage = new PrimaryStage {
    title = "Login"
    height = 300
    width  = 300
    scene = new Scene {
      val border = new BorderPane()


      val loginButton = new Button("Login")

      loginButton.onAction = e => {

        val twitterLogin = new TwitterLogin(AccToken = null, ReqToken = null, myTwitter = null)
        twitterLogin.startlogin()

      }

      val loginCode = new TextField()
      loginCode.promptText="Enter your code"
      val submitButton = new Button("Submit")

      submitButton.onAction = e => {
        println("submit")
      }

      border.center = loginButton

      root = border
    }



  }


}



