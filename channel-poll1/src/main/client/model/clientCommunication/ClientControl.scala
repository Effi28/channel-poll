package main.server.serverCommunication

import java.io.OutputStreamWriter
import java.net.Socket
import client.model.clientCommunication.{ClientMessageSender}
import main.client.model.clientCommunication.ServerHandler
import main.shared.{Message, Statement}
import scalafx.collections.{ObservableHashMap, ObservableHashSet}

object ClientControl {
  val users:ObservableHashSet[String] = new ObservableHashSet[String]()
  val globalChat:ObservableHashMap[Int, Message] = new ObservableHashMap[Int, Message]()
  val groupChat:ObservableHashMap[Int, Message] = new ObservableHashMap[Int, Message]()
  val statements:ObservableHashMap[Int, Statement] = new ObservableHashMap[Int, Statement]()
  var sender:ClientMessageSender = null

  def setupClient(nick:String): Unit ={
    val socket:Socket = new Socket("localhost", 8008)
    new ServerHandler(socket).start()
    sender = new ClientMessageSender(new OutputStreamWriter(socket.getOutputStream, "UTF-8"), nick)
    sender.writeLoginMessage()
  }

  def sendMessage(msg:Message):Unit={
    sender.writeChatMessage(msg)
  }
}