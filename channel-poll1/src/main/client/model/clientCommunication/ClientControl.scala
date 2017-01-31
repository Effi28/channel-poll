package main.server.serverCommunication

import java.io.OutputStreamWriter
import java.net.Socket
import client.model.clientCommunication.{ClientMessageSender}
import main.client.model.clientCommunication.ServerHandler
import main.shared.{Message, Statement, Comment}
import scalafx.collections.{ObservableHashMap, ObservableHashSet}

object ClientControl {
  val users:ObservableHashSet[String] = new ObservableHashSet[String]()
  val globalChat:ObservableHashMap[Int, Message] = new ObservableHashMap[Int, Message]()
  val groupChat:ObservableHashMap[Int, Message] = new ObservableHashMap[Int, Message]()
  val statements:ObservableHashMap[Long, Statement] = new ObservableHashMap[Long, Statement]()
  val comments:ObservableHashMap[Int, Comment] = new ObservableHashMap[Int, Comment]()
  var sender:ClientMessageSender = null

  def setupClient(nick:String): Unit ={
    val socket:Socket = new Socket("localhost", 8008)
    new ServerHandler(socket).start()
    sender = new ClientMessageSender(new OutputStreamWriter(socket.getOutputStream, "UTF-8"), nick)
    sender.writeLoginMessage()
  }

  def sendComment(cmd:Comment): Unit={
    sender.writeStComment(cmd)
  }

  def sendMessage(msg:Message):Unit={
    sender.writeChatMessage(msg)
  }
}