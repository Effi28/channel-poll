package main.server.serverCommunication

import java.net.Socket

import client.model.clientCommunication.ClientMessageSender
import main.client.model.clientCommunication.ServerHandler

import scala.collection.mutable.ArrayBuffer
import main.shared._

import scala.collection.mutable
import scala.collection.mutable.HashMap
import scalafx.collections.{ObservableBuffer, ObservableHashMap}
import scalafx.scene.layout.VBox

object ClientControl {
  val socket:Socket = new Socket("localhost", 8008)
  var nick = ""
  var userid: Long = 0
  val users:ObservableBuffer[String] = new ObservableBuffer[String]()
  val globalChat:ObservableHashMap[Long, Message] = new ObservableHashMap[Long, Message]()
  val groupChat:ObservableHashMap[Long, Message] = new ObservableHashMap[Long, Message]()
  val statements:ObservableBuffer[Statement] = new ObservableBuffer[Statement]()
  //val comments:ObservableHashMap[Statement, ArrayBuffer[Comment]] = new ObservableHashMap[Statement, ArrayBuffer[Comment]]()
  val comments:HashMap[Statement, ObservableBuffer[Comment]] = new HashMap[Statement, ObservableBuffer[Comment]]()
  val polls: ObservableHashMap[Statement, ArrayBuffer[Poll]] = new ObservableHashMap[Statement, ArrayBuffer[Poll]]()
  //@Kathrin: Können wir das auch so machen? ich brauche für die Polls das Observable...
  //val polls: HashMap[Statement, ObservableBuffer[Poll]] = new HashMap[Statement, ObservableBuffer[Poll]]()
  val pollAnswers: ObservableHashMap[Poll, ArrayBuffer[PollAnswer]] = new ObservableHashMap[Poll, ArrayBuffer[PollAnswer]]()
  val chatRooms:ObservableBuffer[Statement] = new ObservableBuffer[Statement]()
  val activityFeed:ObservableBuffer[VBox] = new ObservableBuffer[VBox]()

  def setupClient(user: TwitterUser): Unit ={
    ServerHandler.start()
    nick = user.screenname
    userid = user.userid
    ClientMessageSender.writeLoginMessage()
  }

  def sendComment(comment:Comment): Unit={
    ClientMessageSender.writeStComment(comment)
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

  def sendPoll(poll: Poll): Unit = {
    //TODO:polls behandeln und speichern
  }
}