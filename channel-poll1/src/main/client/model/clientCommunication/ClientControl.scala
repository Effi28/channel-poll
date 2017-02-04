package main.server.serverCommunication

import java.net.Socket
import client.model.clientCommunication.{ClientMessageSender}
import main.client.model.clientCommunication.ServerHandler
import main.shared.{Comment, Message, Statement}
import scalafx.collections.{ObservableBuffer, ObservableHashMap}
import scalafx.scene.layout.{VBox}

object ClientControl {
  val socket:Socket = new Socket("localhost", 8008)
  var nick = ""
  val users:ObservableBuffer[String] = new ObservableBuffer[String]()
  val globalChat:ObservableHashMap[Int, Message] = new ObservableHashMap[Int, Message]()
  val groupChat:ObservableHashMap[Int, Message] = new ObservableHashMap[Int, Message]()
  val statements:ObservableBuffer[Statement] = new ObservableBuffer[Statement]()
  val comments:ObservableHashMap[Int, Comment] = new ObservableHashMap[Int, Comment]()
  val chatRooms:ObservableBuffer[Statement] = new ObservableBuffer[Statement]()
  val activityFeed:ObservableBuffer[VBox] = new ObservableBuffer[VBox]()

  def setupClient(nickTemp:String): Unit ={
    ServerHandler.start()
    nick = nickTemp
    ClientMessageSender.writeLoginMessage()
  }

  def sendComment(cmd:Comment): Unit={
    ClientMessageSender.writeStComment(cmd)
  }

  def sendMessage(msg:Message):Unit={
    ClientMessageSender.writeChatMessage(msg)
  }

  def logout(): Unit ={
    ClientMessageSender.writeLogout()
  }

  def close()={
    System.exit(0)
  }
}