package main.server

import java.net.{ServerSocket, SocketException}
import java.util.concurrent.{ExecutorService, Executors}

import scala.collection.mutable
import scala.collection.mutable.MutableList
import scala.collection.mutable.HashMap

class ServerControl(port: Int, poolSize: Int) extends Runnable {
  val serverSocket = new ServerSocket(port)
  val pool: ExecutorService = Executors.newFixedThreadPool(poolSize)
  val connectedHandler:HashMap[String, ClientHandler] = new HashMap[String, ClientHandler]
  val chatRooms:HashMap[String, HashMap[String, ClientHandler]] = new mutable.HashMap[String, HashMap[String, ClientHandler]];

  def run() {
    try {
      while (true) {
      // This will block until a connection comes in.
      val socket = serverSocket.accept()
      pool.execute(new ClientHandler(socket, this))
      }
    }
    finally {
      pool.shutdown()
    }
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

  def broadcastGlobalMessage(sender:String, stamp:String, msg:String): Unit = {
    for ((k, v) <- connectedHandler) {
      if(!k.equals(sender)){
        v.sender.writeChatMessage(sender, msg, stamp)
      }
    }
  }

  def broadcastGroupMessage(sender:String, stamp:String, msg:String): Unit = {

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
}