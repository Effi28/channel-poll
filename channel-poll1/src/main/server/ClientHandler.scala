package main.server

import java.io.{BufferedReader, InputStreamReader, OutputStreamWriter}
import java.net.{Socket, SocketException}

class ClientHandler(socket:Socket, server:ServerControl) extends Runnable{
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
      case se:SocketException => server.removeClient(this)
    }
  }

  def checkLogin(nick:String): Unit = {
    server.checkLogin(nick:String, this)
  }

  def checkGlobalMessage(sender:String, stamp:String, msg:String): Unit ={
    server.broadcastGlobalMessage(sender, stamp, msg)
  }

  def checkGroupMessage(sender:String, stamp:String, msg:String, rcv:String): Unit ={
    server.broadcastGroupMessage(sender, stamp, msg)
  }

  def handleLogout(nick:String): Unit ={
    server.removeClient(nick)
  }
}
