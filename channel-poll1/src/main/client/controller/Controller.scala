package main.client.controller

import main.client.view.LoginView
import main.server.serverCommunication.ClientControl
import main.shared.{Message, Statement, TwitterUser}

import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.scene.layout.VBox

object Controller {
  def sendMessage(msg: Message): Unit = {
    ClientControl.sendMessage(msg)
  }

  def setupClient(user: TwitterUser): Unit = {
    ClientControl.setupClient(user)
  }

  def exitLoginView(): Unit = {
    Platform.runLater(new Thread -> LoginView.exit())
  }

  def getStatements():ObservableBuffer[Statement]= {
    ClientControl.statements
  }

  def getUsers():ObservableBuffer[String]= {
    ClientControl.users
  }

  def getChatRooms():ObservableBuffer[Statement]= {
    ClientControl.chatRooms
  }

  def getActivityFeedback():ObservableBuffer[VBox]= {
    ClientControl.activityFeed
  }

  def logout() {
    ClientControl.logout()
  }
}
