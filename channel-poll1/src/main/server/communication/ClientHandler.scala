package main.server.communication

import java.io.{BufferedReader, InputStreamReader, OutputStreamWriter}
import java.net.{Socket, SocketException}

import main.server.Server
import main.shared.data.{Comment, Poll, PollAnswer, Statement}

final class ClientHandler(socket:Socket) extends Runnable{
  private val rec: ServerMessageReceiver = new ServerMessageReceiver(new BufferedReader(new InputStreamReader(socket.getInputStream, "UTF-8")), this)
  var sender:ServerMessageSender = new ServerMessageSender(new OutputStreamWriter(socket.getOutputStream, "UTF-8"))

  def run(): Unit = {
    try{
      while (true) {
        rec.readMessage()
      }
    }
    catch{
      case se:SocketException => Server.removeClient(this)
    }
  }

  def checkLogin(nick:String): Unit = {
    Server.checkLogin(nick:String, this)
  }

  def handleLogout(nick:String): Unit ={
    Server.removeClient(nick)
  }

  def handleStatement(statement:Statement): Unit ={
    Server.broadcastStatement(statement)
  }

  def handleComment(comment:Comment): Unit = {
    Server.broadcastComment(comment)
  }

  def handlePoll(poll:Poll): Unit = {
    Server.broadcastPoll(poll)
  }

  def handlePollAnswer(pollAnswer: PollAnswer): Unit = {
    Server.broadcastPollAnswers(pollAnswer)
  }

  def handleSubscribe(statementID:Long, nick:String): Unit ={
    Server.handleSubscribe(statementID, nick)
  }

  def handleUnsubscribe(statementID:Long, nick:String): Unit ={
    Server.handleUnSubscribe(statementID, nick)
  }
}
