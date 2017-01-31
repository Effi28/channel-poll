package main.server.serverCommunication

import java.io.OutputStreamWriter
import java.net.Socket

import client.model.clientCommunication.{ClientMessageReceiver, ClientMessageSender}
import main.client.model.clientCommunication.ServerHandler
import main.shared.{Message, Statement}

import scalafx.collections.{ObservableHashMap, ObservableHashSet}



object ClientControl {
  val users:ObservableHashSet[String] = new ObservableHashSet[String]()
  val globalChat:ObservableHashMap[Int, Message] = new ObservableHashMap[Int, Message]()
  val groupChat:ObservableHashMap[Int, Message] = new ObservableHashMap[Int, Message]()
  val statements:ObservableHashMap[Int, Statement] = new ObservableHashMap[Int, Statement]()
  var sender:ClientMessageSender = null
  var nick = ""

  def setupClient(address:String, port:Int): Unit ={
    val socket:Socket = new Socket(address, port)
    new ServerHandler(socket).start()
    sender = new ClientMessageSender(new OutputStreamWriter(socket.getOutputStream, "UTF-8"), nick)
    sender.writeLoginMessage()
    }

  def sendMessage(msg:Message):Unit={ sender.writeChatMessage(msg)}
  def sendNick(nick1:String): Unit ={
   nick = nick1
  }
}