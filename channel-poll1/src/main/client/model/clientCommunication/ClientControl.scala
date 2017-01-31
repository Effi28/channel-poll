package main.server.serverCommunication


import java.io.OutputStreamWriter
import java.net.Socket
import javafx.beans.InvalidationListener
import javafx.collections.{ArrayChangeListener, ObservableArray}

import client.model.clientCommunication.{ClientMessageReceiver, ClientMessageSender}
import main.client.model.clientCommunication.ServerHandler

import scalafx.collections.ObservableHashSet



object ClientControl {
  val users:ObservableHashSet[String] = new ObservableHashSet[String]()
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