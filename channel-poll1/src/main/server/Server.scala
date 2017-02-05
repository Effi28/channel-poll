package main.server

import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.{ExecutorService, Executors}

import main.shared.{Comment, Message, Statement}
import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer

object Server {
  val connectedHandler:HashMap[String, ClientHandler] = new HashMap[String, ClientHandler]
  val chatRooms:HashMap[String, HashMap[String, ClientHandler]] = new HashMap[String, HashMap[String, ClientHandler]];
  val statements:HashMap[Long, Statement] = new HashMap[Long, Statement]
  val comments:HashMap[Statement, ArrayBuffer[Comment]] = new HashMap[Statement, ArrayBuffer[Comment]]
  var chatID = 0

  def main(args: Array[String]): Unit = {
    startTwitterStream()

    val pool: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    val serverSocket = new ServerSocket(8008)
    try {
      while (true) {
      val socket:Socket = serverSocket.accept()
      pool.execute(new ClientHandler(socket))
      }
    }
    finally {
      pool.shutdown()
    }
  }

  def startTwitterStream(): Unit = {
    val accessStreamer = new TwitterAccess
    accessStreamer.streamingThread.start()

    // start dequeue thread for getting statements from queue
    val statementGetter = new QueueGetter
    statementGetter.dequeueThread.start()
  }

  def checkLogin(nick:String, client:ClientHandler): Unit ={
    if(connectedHandler.keySet.contains(nick)){
      client.sender.writeLoginFailed(nick)
    }
    else{
      connectedHandler += (nick -> client)
      broadcastLogin(client, nick)
    }
  }

  def broadcastLogin(client:ClientHandler, nick:String): Unit ={
    for ((k, v) <- connectedHandler) {
      if(k.equals(nick)){
        v.sender.writeLoginSuccess(connectedHandler.keys)
      }
      else{
        v.sender.writeNewLogin(nick)
      }
    }
  }

  def broadcastGlobalMessage(message:Message): Unit = {
    for ((k, v) <- connectedHandler) {
      if(!k.equals(message.sender)){
        v.sender.writeChatMessage(message)
      }
    }
    chatID = chatID + 1;
  }

  def broadcastGroupMessage(message:Message): Unit = {
  //TODO
  }

  def removeClient(clientHandler: ClientHandler): Unit ={
    var user:String = null
    for ((k, v) <- connectedHandler) {
      if(v.equals(clientHandler)){
        connectedHandler.remove(k)
        user = k
      }
  }
    for ((k, v) <- connectedHandler) {
      if(v.equals(clientHandler)){
        v.sender.writeDisconnect(user)
      }
    }
  }

  def removeClient(user:String): Unit ={
    connectedHandler.remove(user)
    for ((k, v) <- connectedHandler) {
      v.sender.writeDisconnect(user)
    }
  }

  def broadcastStatement(statement:Statement): Unit ={
    statements += statement.ID -> statement
    println(statement)
    for ((k, v) <- connectedHandler) {
      v.sender.writeStatement(statement)
    }
  }

  def broadcastComment(comment:Comment): Unit ={
    comments.get(comment.statement).get += comment
    for ((k, v) <- connectedHandler) {
      v.sender.writeComment(comment)
    }
  }
}