/**
  * Created by Brenda on 30.01.17.
  */
package main.client.view

import java.awt.Desktop

import main.client.controller.Controller

import scalafx.application.{JFXApp, Platform}
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
        if(Desktop.isDesktopSupported){
          Desktop.getDesktop.browse(loginURL.toURI)
        }else{
          println(loginURL)
        }
      }
      val codeTextField = new TextField()
      codeTextField.promptText="Enter your code"
      val submitButton = new Button("Submit")
      /**
        * Hier bei doLogin kommt (true, Screenname) zurueck,
        * wenn login erfolgreich war
        */
      submitButton.onAction = e => {
        //TODO: Fehler anzeigen, falls kein Code eingegeben wurde
        val code = codeTextField.getText
        Controller.setupClient(twitterLogin.doLogin(code)._2)
      }
      border.center = new VBox(loginButton, codeTextField, submitButton)
      root = border
    }
  }

  def exit():Unit={
    stage = ClientView.getStage()
  }

}



