package main.server.communication

import java.io.{BufferedReader, InputStreamReader, OutputStreamWriter}
import java.net.{Socket, SocketException}

import main.server.Server
import main.shared.data._

final class ClientHandler(socket: Socket) extends Runnable {
  private val rec: ServerMessageReceiver = new ServerMessageReceiver(new BufferedReader(new InputStreamReader(socket.getInputStream, "UTF-8")), this)
  var sender: ServerMessageSender = new ServerMessageSender(new OutputStreamWriter(socket.getOutputStream, "UTF-8"))

  def run(): Unit = {
    try {
      while (true) {
        rec.readMessage()
      }
    }
    catch {
      case se: SocketException => Server.removeClient(this)
    }
  }

  def handleLogin(user: TwitterUser): Unit = {
    Server.checkLogin(user, this)
  }

  def handleLogout(user: TwitterUser): Unit = {
    Server.removeClient(this)
  }

  def handleStatement(statement: Statement): Unit = {
    Server.broadcastStatement(statement)
  }

  def handleComment(comment: Comment): Unit = {
    Server.broadcastComment(comment)
  }

  def handlePoll(poll: Poll): Unit = {
    Server.broadcastPoll(poll)
  }

  def handlePollAnswer(pollAnswer: PollAnswer): Unit = {
    Server.broadcastPollAnswers(pollAnswer)
  }

  def handleSubscribe(statementID: Long, user: TwitterUser):Unit = {
    Server.handleSubscribe(statementID, user)
  }

  def handleUnsubscribe(statementID: Long, user: TwitterUser): Unit = {
    Server.handleUnSubscribe(statementID, user)
  }
}
