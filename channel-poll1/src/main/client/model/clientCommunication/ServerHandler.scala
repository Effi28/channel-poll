package main.client.model.clientCommunication

import client.model.clientCommunication.ClientMessageReceiver
import main.server.serverCommunication.ClientControl
import main.shared._

import scala.collection.mutable.ArrayBuffer


object ServerHandler extends Thread{
  def message = (Thread.currentThread.getName() + "\n").getBytes

  override def run(): Unit = {
    while (true) {
      ClientMessageReceiver.readMessage()
    }
  }

  def handleLogin(nick: String): Unit = {
    ClientControl.users += nick
  }

  def handleLogout(nick: String): Unit = {
    ClientControl.users -= nick
  }

  def handlePoll(poll: Poll): Unit = {
    //val statement = ClientControl.statements
    ClientControl.polls.get(poll.statementID)
    // todo @Brenda wie bekomme ich denn hier die statements dass ich
    //mit der id das statement finden kann ??
  }

  def handlePollAnswer(pollAnswer: PollAnswer): Unit = {
    //ClientControl.pollAnswers.get(pollAnswer.pollID).get += pollAnswer
    //todo
  }

  def handleStatement(statement:Statement):Unit = {
    ClientControl.statements += statement
  }

  def handleComment(comment: Comment): Unit = {
    if(!ClientControl.comments.contains(comment.statementID)){
      ClientControl.comments += comment.statementID -> new ArrayBuffer[Comment]()
    }
    ClientControl.comments.get(comment.statementID).get += comment
  }

  def handleGlobalChat(message: Message): Unit = {
    ClientControl.globalChat += message.statementID -> message
  }

  def handleGroupChat(message: Message): Unit = {
    ClientControl.groupChat += message.statementID -> message
  }
}
