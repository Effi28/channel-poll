package main.server.serverCommunication

import java.io.OutputStreamWriter
import java.net.Socket

import client.model.clientCommunication.ClientMessageSender
import main.client.model.clientCommunication.ServerHandler
import main.shared.{ChatMessage, Message, Poll, Statement}

import scala.collection.mutable
import scala.collection.mutable.HashMap
import scalafx.collections.{ObservableBuffer, ObservableHashMap, ObservableHashSet}
import scalafx.scene.layout.{HBox, VBox}

object ClientControl {
  val users: ObservableBuffer[String] = new ObservableBuffer[String]()
  val globalChat: ObservableHashMap[Long, Message] = new ObservableHashMap[Long, Message]()
  val groupChat: ObservableHashMap[Long, Message] = new ObservableHashMap[Long, Message]()
  //val statements:ObservableHashMap[Long, Statement] = new ObservableHashMap[Long, Statement]()

  val statements: ObservableBuffer[Statement] = new ObservableBuffer[Statement]()

  //val chatMessages:ObservableHashMap[Int, ChatMessage] = new ObservableHashMap[Int, ChatMessage]()
  var sender: ClientMessageSender = null


  val chatRooms: ObservableBuffer[Statement] = new ObservableBuffer[Statement]()
  val chatMessages: HashMap[Long, ObservableBuffer[Message]] = new HashMap[Long, ObservableBuffer[Message]]()

  val activityFeed: ObservableBuffer[VBox] = new ObservableBuffer[VBox]()

  val statementPolls: HashMap[Long, ObservableBuffer[Poll]] = new HashMap[Long, ObservableBuffer[Poll]]()


  def setupClient(nick: String): Unit = {
    val socket: Socket = new Socket("localhost", 8008)
    new ServerHandler(socket).start()
    sender = new ClientMessageSender(new OutputStreamWriter(socket.getOutputStream, "UTF-8"), nick)
    sender.writeLoginMessage()
  }

  /*
  def sendChatMessage(msg:ChatMessage): Unit={
    sender.writeStComment(msg)
  }
  */

  def sendMessage(msg: Message): Unit = {
    sender.writeChatMessage(msg)
  }

  def sendPoll(poll: Poll): Unit = {
    //TODO:polls behandeln und speichern
  }
}