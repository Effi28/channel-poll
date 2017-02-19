package main.client.controller

import main.client.model.ClientControl
import main.client.view.LoginView
import main.shared.data._
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer

final object Controller {
  def setupClient(user: TwitterUser) = ClientControl.setupClient(user)

  def exitLoginView = Platform.runLater(new Thread -> LoginView.exit)

  def logout = ClientControl.logout

  def sendComment(comment: Comment) = ClientControl.sendData(comment)

  def sendPoll(poll: Poll) = ClientControl.sendData(poll)

  def sendPollAnswer(pollAnswer: PollAnswer) = ClientControl.sendData(pollAnswer)

  def statementContainsComments(statement: Statement) = ClientControl.comments contains statement.ID

  def chatRoomsContainStatement(statement: Statement) = ClientControl.chatRooms contains statement

  def statementContainsPolls(statement: Statement) = ClientControl.polls contains statement.ID

  def setStatementInPolls(statement: Statement) = ClientControl.polls += statement.ID -> new ObservableBuffer[Poll]

  def setStatementInComments(statement: Statement) = ClientControl.comments += statement.ID -> new ObservableBuffer[Comment]

  //def setStatementInChatRooms(statement: Statement) = ClientControl.chatRooms += statement -> new ObservableBuffer[TwitterUser]

  def getTwitterUser = ClientControl.user

  def getStatements = ClientControl.statements

  def getUsers = ClientControl.users

  def getPolls = ClientControl.polls

  def getComments = ClientControl.comments

  def getChatRooms = ClientControl.chatRooms

  //def getUserChatRooms(user: TwitterUser) = ClientControl.userChatRooms.get(user).get

  //def getChatMembers(statement: Statement) = ClientControl.chatRooms.get(statement).get

  def getPollsForStatement(statement: Statement): ObservableBuffer[Poll] = {
    if (!ClientControl.polls.contains(statement.ID)) {
      ClientControl.polls += statement.ID -> new ObservableBuffer[Poll]
    }
    ClientControl.polls.get(statement.ID).get
  }

  def getNumberOfPolls(statement: Statement): Int = {
    ClientControl.polls.get(statement.ID).get.size
  }


  /*
  def addStatementToChatRooms(statement: Statement): Unit = {
    ClientControl.chatRooms.put(statement, new ObservableBuffer[TwitterUser]())
  }
  */


  def getCommentsForStatement(statement: Statement): ObservableBuffer[Comment] = {
    if (!ClientControl.comments.contains(statement.ID)) {
      ClientControl.comments += statement.ID -> new ObservableBuffer[Comment]
    }
    ClientControl.comments.get(statement.ID).get
  }


  def getNumberOfComments(statement: Statement): Int = {
    ClientControl.comments.get(statement.ID).get.size
  }
}
