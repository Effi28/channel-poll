package main.client.controller

import main.client.model.ClientControl
import main.client.view.LoginView
import main.shared.data._
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer

final object Controller {
  def setupClient(user: TwitterUser) = ClientControl.setupClient(user)
  def exitLoginView =  Platform.runLater(new Thread -> LoginView.exit())
  def getStatements = ClientControl.statements
  def getUsers = ClientControl.users
  def getPolls = ClientControl.polls
  def pollsContainStatement(statement: Statement) = ClientControl.polls.contains(statement.ID)
  def setStatementInPolls(statement: Statement) = ClientControl.polls.put(statement.ID, new ObservableBuffer[Poll]())
  //def getChatRooms(): ObservableBuffer[Statement] = ClientControl.chatRooms
  def chatRoomsContainStatement(statement: Statement)= ClientControl.chatRooms.contains(statement)
  def logout = ClientControl.logout
  def sendComment(comment: Comment) = ClientControl.sendComment(comment)
  def sendPoll(poll: Poll) = ClientControl.sendPoll(poll)
  def subscribe(statement: Statement, subscribe: Boolean)= ClientControl.subscribe(statement, subscribe)
  def sendPollAnswer(pollAnswer: PollAnswer) = ClientControl.sendPollAnswer(pollAnswer);
  def getTwitterUser = ClientControl.user
  def getUserChatRooms(user: TwitterUser) = ClientControl.userChatRooms.get(user).get
  def commentsContainStatement(statement: Statement) = ClientControl.comments.contains(statement.ID)
  def setStatementInComments(statement: Statement) = ClientControl.comments += statement.ID -> new ObservableBuffer[Comment]
  def addStatementToChatRooms(statement: Statement) = ClientControl.chatRooms += statement -> new ObservableBuffer[TwitterUser]()
  def getChatRooms = ClientControl.chatRooms
  def getChatMembers(statement: Statement) = ClientControl.chatRooms.get(statement).get
  def getComments = ClientControl.comments

  def getPollsForStatement(statement: Statement): ObservableBuffer[Poll] = {
    if (!ClientControl.polls.contains(statement.ID)) {
      ClientControl.polls += statement.ID -> new ObservableBuffer[Poll]()
    }
    ClientControl.polls.get(statement.ID).get
  }

  def getCommentsForStatement(statement: Statement): ObservableBuffer[Comment] = {
    if (!ClientControl.comments.contains(statement.ID)) {
      ClientControl.comments += statement.ID -> new ObservableBuffer[Comment]()
    }
    ClientControl.comments.get(statement.ID).get
  }
}
