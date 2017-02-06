package main.client.controller

import main.client.view.LoginView
import main.server.serverCommunication.ClientControl
import main.shared.{Comment, Message, Statement, TwitterUser}

import scala.collection.mutable.HashMap
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

  def getComments():HashMap[Statement, ObservableBuffer[Comment]]={
    ClientControl.comments
  }

  def getCommentsForStatement(statement: Statement):ObservableBuffer[Comment]={
    ClientControl.comments.get(statement).get
  }

  def commentsContainStatement(statement: Statement):Boolean={
    ClientControl.comments.contains(statement)
  }

  def setStatementForComments(statement: Statement):Unit={
    ClientControl.comments.put(statement, new ObservableBuffer[Comment]())
  }


  def logout() {
    ClientControl.logout()
  }
}
