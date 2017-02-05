package main.server.serverCommunication

import java.net.Socket

import client.model.clientCommunication.ClientMessageSender
import main.client.model.clientCommunication.ServerHandler

import scala.collection.mutable.ArrayBuffer
import main.shared.{Comment, Message, Poll, Statement}

import scalafx.collections.{ObservableBuffer, ObservableHashMap}
import scalafx.scene.layout.VBox

object ClientControl {
  val socket:Socket = new Socket("localhost", 8008)
  var nick = ""
  val users:ObservableBuffer[String] = new ObservableBuffer[String]()
  val globalChat:ObservableHashMap[Long, Message] = new ObservableHashMap[Long, Message]()
  val groupChat:ObservableHashMap[Long, Message] = new ObservableHashMap[Long, Message]()
  val statements:ObservableBuffer[Statement] = new ObservableBuffer[Statement]()
  val comments:ObservableHashMap[Statement, ArrayBuffer[Comment]] = new ObservableHashMap[Statement, ArrayBuffer[Comment]]()
  val chatRooms:ObservableBuffer[Statement] = new ObservableBuffer[Statement]()
  val activityFeed:ObservableBuffer[VBox] = new ObservableBuffer[VBox]()

  def setupClient(nickTemp:String): Unit ={
    ServerHandler.start()
    nick = nickTemp
    ClientMessageSender.writeLoginMessage()
  }

  //def sendComment(cmd:Comment): Unit={
    //ClientMessageSender.writeStComment(cmd)
  //}



  def sendMessage(msg:Message):Unit={
    ClientMessageSender.writeChatMessage(msg)
  }

  def logout(): Unit ={
    ClientMessageSender.writeLogout()
  }

  def close()={
    System.exit(0)
  }

  def sendPoll(poll: Poll): Unit = {
    //TODO:polls behandeln und speichern
  }
}