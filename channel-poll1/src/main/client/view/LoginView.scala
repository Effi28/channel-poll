/**
  * Created by Brenda on 30.01.17.
  */
package main.client.view

import java.awt.Desktop
import main.client.controller.Controller
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.{Button, TextField}
import scalafx.scene.layout.BorderPane
import scalafx.scene.paint.Color._
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.Label
import scalafx.scene.layout.GridPane
import scalafx.scene.text.Text

/**
  * Created by Brenda on 30.01.17.
  */

final object LoginView extends JFXApp {
  stage = new PrimaryStage {
    title = "Login"

    scene = new Scene {
      val border = new BorderPane()

      val grid = new GridPane()

      grid.vgap = 10
      grid.hgap = 10

      grid.margin = Insets(15)

      val welcomeMessage = new Label("Welcome to ChannelPoll")
      welcomeMessage.maxWidth = Double.MaxValue
      welcomeMessage.alignment = Pos.Center
      welcomeMessage.style = "-fx-font-size: 18pt;"
      grid.addRow(0, welcomeMessage)

      val loginButton = new Button("Login")
      loginButton.maxWidth = Double.MaxValue
      loginButton.alignment = Pos.Center
      loginButton.onAction = e => {
        val loginURL = TwitterLogin.startLogin()
        if (Desktop.isDesktopSupported) {
          Desktop.getDesktop.browse(loginURL.toURI)
        } else {
          println(loginURL)
        }
        loginButton.disable = true
        codeField.disable = false
        submitButton.disable = false
      }

      grid.addRow(1, loginButton)


      val codeField = new TextField()
      codeField.promptText = "Enter your code..."
      codeField.maxWidth = Double.MaxValue
      codeField.alignment = Pos.Center
      codeField.disable = true
      grid.addRow(2, codeField)


      val submitButton = new Button("Submit")
      submitButton.maxWidth = Double.MaxValue
      submitButton.alignment = Pos.Center
      submitButton.disable = true
      submitButton.onAction = e => {
        if (codeField.getText.size == 0) {
          warningMessage.visible = true

        }
        else {
          val code = codeField.getText
          Controller.setupClient(TwitterLogin.doLogin(code)._2)
        }
      }


      grid.addRow(3, submitButton)

      val warningMessage = new Text("Please enter the login code!")
      warningMessage.visible = false
      warningMessage.fill = Red
      grid.addRow(4, warningMessage)


      border.center = grid
      root = border
    }
  }

  def exit = ClientView.getStage()
}



