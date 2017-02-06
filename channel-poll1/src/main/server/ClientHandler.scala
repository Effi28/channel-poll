package main.server

import java.io.{BufferedReader, InputStreamReader, OutputStreamWriter}
import java.net.{Socket, SocketException}

import main.shared.{Comment, Poll, PollAnswer, Statement}

import scala.collection.mutable.ArrayBuffer

class ClientHandler(socket:Socket) extends Runnable{
  val rec: ServerMessageReceiver = new ServerMessageReceiver(new BufferedReader(new InputStreamReader(socket.getInputStream, "UTF-8")), this)
  var sender:ServerMessageSender = new ServerMessageSender(new OutputStreamWriter(socket.getOutputStream, "UTF-8"))
  def message = (Thread.currentThread.getName() + "\n").getBytes

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

  def handleSubscribe(statementID:Long, nick:String): Unit ={
    val tempClient:ClientHandler =  Server.connectedHandler(nick)
    if(!Server.chatRooms.contains(tempClient)){
      Server.chatRooms += tempClient -> new ArrayBuffer[Long]
    }
    Server.chatRooms.get(tempClient).get += statementID
  }

  def handleUnsubscribe(statementID:Long, nick:String): Unit ={
    val tempClient:ClientHandler =  Server.connectedHandler(nick)
    Server.chatRooms.get(tempClient).get -= statementID

    if(Server.chatRooms.get(tempClient).size == 0){
      Server.chatRooms -= tempClient
    }
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
}
