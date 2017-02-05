package main.client.controller

import main.client.view.LoginView
import main.server.serverCommunication.ClientControl
import main.shared.Message
import scalafx.application.Platform

object Controller {
  def sendMessage(msg: Message): Unit = {
    ClientControl.sendMessage(msg)
  }

  def setupClient(nick: String): Unit = {
    ClientControl.setupClient(nick)
  }

  def exitLoginView(): Unit = {
    Platform.runLater(new Thread -> LoginView.exit())
  }
}
