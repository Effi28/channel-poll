package main.client.controller

import main.client.view.LoginView
import main.server.serverCommunication.ClientControl
import main.shared._

import scala.collection.mutable.HashMap
import scalafx.application.Platform
import scalafx.collections.{ObservableBuffer, ObservableHashMap}
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

  def getStatements(): ObservableBuffer[Statement] = {
    ClientControl.statements
  }

  def getUsers(): ObservableBuffer[String] = {
    ClientControl.users
  }

  //def getPolls():ObservableHashMap[Long, ArrayBuffer[Poll]]= {
  //ClientControl.polls
  //}

  def getPolls(): HashMap[Long, ObservableBuffer[Poll]] = {
    ClientControl.polls
  }

  def getPollsForStatement(statement: Statement): ObservableBuffer[Poll] = {
    ClientControl.polls.get(statement.ID).get
  }

  def pollsContainStatement(statement: Statement): Boolean = {
    ClientControl.polls.contains(statement.ID)
  }

  def setStatementInPolls(statement: Statement): Unit = {
    ClientControl.polls.put(statement.ID, new ObservableBuffer[Poll]())
  }

  //def getComments():ObservableHashMap[Long, ArrayBuffer[Comment]]= {
  //ClientControl.comments
  //}

  def getChatRooms(): ObservableBuffer[Statement] = {
    ClientControl.chatRooms
  }

  def getActivityFeedback(): ObservableBuffer[VBox] = {
    ClientControl.activityFeed
  }

  def getComments(): HashMap[Long, ObservableBuffer[Comment]] = {
    ClientControl.comments
  }

  def getCommentsForStatement(statement: Statement): ObservableBuffer[Comment] = {
    ClientControl.comments.get(statement.ID).get
  }

  def commentsContainStatement(statement: Statement): Boolean = {
    ClientControl.comments.contains(statement.ID)
  }

  def setStatementInComments(statement: Statement): Unit = {
    ClientControl.comments.put(statement.ID, new ObservableBuffer[Comment]())
  }


  def logout() {
    ClientControl.logout()
  }

  def sendComment(comment: Comment): Unit = {
    ClientControl.sendComment(comment)
  }

  def sendPoll(poll: Poll): Unit = {
    ClientControl.sendPoll(poll)
  }

  def getTwitterUser(): TwitterUser = {
    ClientControl.user
  }

  def subscribe(statement: Statement): Unit = {
    ClientControl.subscribe(statement)
  }

  def unsubscribe(statement: Statement): Unit = {
    ClientControl.unsubscribe(statement)
  }
}
