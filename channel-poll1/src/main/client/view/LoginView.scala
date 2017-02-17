/**
  * Created by Brenda on 30.01.17.
  */
package main.client.view

import java.awt.Desktop
import main.client.controller.Controller
import scalafx.application.{JFXApp}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.{Button, TextField}
import scalafx.scene.layout.{BorderPane, VBox}

/**
  * Created by Brenda on 30.01.17.
  */

final object LoginView extends JFXApp {
  stage = new PrimaryStage {
    title = "Login"
    height = 300
    width = 300
    scene = new Scene {
      //stylesheets = List(getClass.getResource("material-fx-v0_3.css").toExternalForm)
      val border = new BorderPane()
      val loginButton = new Button("Login")
      loginButton.onAction = e => {
        val loginURL = TwitterLogin.startLogin()
        if (Desktop.isDesktopSupported) {
          Desktop.getDesktop.browse(loginURL.toURI)
        } else {
          println(loginURL)
        }




      }
      val codeTextField = new TextField()
      codeTextField.promptText = "Enter your code"
      val submitButton = new Button("Submit")
      /**
        * Hier bei doLogin kommt (true, Screenname) zurueck,
        * wenn login erfolgreich war
        *
        *
        */
      submitButton.onAction = e => {
        if (codeTextField.getText.size == 0) {
          //TODO Fehlermeldung anzeigen, dass codefeld nicht leer sein darf
        }
        val code = codeTextField.getText
        Controller.setupClient(TwitterLogin.doLogin(code)._2)
      }
      border.center = new VBox(loginButton, codeTextField, submitButton)
      root = border
    }
  }
  def exit = ClientView.getStage()
}



