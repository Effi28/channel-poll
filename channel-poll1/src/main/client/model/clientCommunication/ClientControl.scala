package main.server.serverCommunication


import java.io.OutputStreamWriter
import java.net.Socket

import client.model.clientCommunication.{ClientMessageReceiver, ClientMessageSender}
import main.client.model.clientCommunication.ServerHandler

import scala.collection.mutable.ListBuffer


object ClientControl {
  val users:ListBuffer[String] = new ListBuffer[String];
  var sender:ClientMessageSender = null
  var nick = ""

  def setupClient(address:String, port:Int): Unit ={
    val socket:Socket = new Socket(address, port)
    new ServerHandler(socket).start()
    sender = new ClientMessageSender(new OutputStreamWriter(socket.getOutputStream, "UTF-8"), nick)
    sender.writeLoginMessage()
    }

  def sendMessage(msg:String):Unit={ sender.writeChatMessage(msg)}
  def sendMessage(msg:String, recv:String):Unit = {sender.writeChatMessage(msg, recv)}

  def sendNick(nick1:String): Unit ={
   nick = nick1
  }
}