package main.client.controller

import main.client.model.ClientControl
import main.client.view.LoginView
import main.shared.data._
import scala.collection.mutable.HashMap
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer

final object Controller {
  def setupClient(user: TwitterUser): Unit = {
    ClientControl.setupClient(user)
  }

  def exitLoginView(): Unit = {
    Platform.runLater(new Thread -> LoginView.exit())
  }

  def getStatements(): ObservableBuffer[Statement] = {
    ClientControl.statements
  }

  def getUsers(): ObservableBuffer[TwitterUser] = {
    ClientControl.users
  }

  def getPolls(): HashMap[Long, ObservableBuffer[Poll]] = {
    ClientControl.polls
  }

  def getPollsForStatement(statement: Statement): ObservableBuffer[Poll] = {
    if (!ClientControl.polls.contains(statement.ID)) {
      ClientControl.polls += statement.ID -> new ObservableBuffer[Poll]()
    }
    ClientControl.polls.get(statement.ID).get
  }

  def pollsContainStatement(statement: Statement): Boolean = {
    ClientControl.polls.contains(statement.ID)
  }

  def setStatementInPolls(statement: Statement): Unit = {
    ClientControl.polls.put(statement.ID, new ObservableBuffer[Poll]())
  }

  def getChatRooms(): ObservableBuffer[Statement] = {
    ClientControl.chatRooms
  }

  def getComments(): HashMap[Long, ObservableBuffer[Comment]] = {
    ClientControl.comments
  }

  def getCommentsForStatement(statement: Statement): ObservableBuffer[Comment] = {
    if (!ClientControl.comments.contains(statement.ID)) {
      ClientControl.comments += statement.ID -> new ObservableBuffer[Comment]()
    }
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

  def sendPollAnswer(pollAnswer: PollAnswer):Unit={
    ClientControl.sendPollAnswer(pollAnswer);
  }

  def getTwitterUser(): TwitterUser = {
    ClientControl.user
  }

  def subscribe(statement: Statement, subscribe:Boolean): Unit = {
    ClientControl.subscribe(statement, subscribe)
  }
}
