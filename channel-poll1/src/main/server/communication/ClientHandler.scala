package main.server.communication

import java.io.{BufferedReader, InputStreamReader, OutputStreamWriter}
import java.net.{Socket, SocketException}
import main.server.Server
import main.shared.data._

final case class ClientHandler(socket: Socket) extends Runnable {
  private val rec = new ServerMessageReceiver(new BufferedReader(new InputStreamReader(socket.getInputStream, "UTF-8")), this)
  var sender = new ServerMessageSender(new OutputStreamWriter(socket.getOutputStream, "UTF-8"))

  def run {
    try {
      while (!socket.isClosed) {rec.readMessage}
    }
    catch {
      case se: SocketException => Server.removeClient(this)
    }
  }

  def handleLogin(user: TwitterUser)=Server.checkLogin(user, this)
  def handleLogout(user: TwitterUser)=Server.removeClient(this)
  def handleStatement(statement: Statement)=Server.broadcastStatement(statement)
  def handleComment(comment: Comment)=Server.broadcastComment(comment)
  def handlePoll(poll: Poll)=Server.broadcastPoll(poll)
  def handlePollAnswer(pollAnswer: PollAnswer)=Server.broadcastPollAnswers(pollAnswer)
  def handleSubscribe(statementID: Long, user: TwitterUser) = Server.handleSubscribe(statementID, user)
  def handleUnsubscribe(statementID: Long, user: TwitterUser)=Server.handleUnSubscribe(statementID, user)
}
