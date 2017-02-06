package main.server.serverCommunication

import java.net.Socket

import client.model.clientCommunication.ClientMessageSender
import main.client.model.clientCommunication.ServerHandler
import main.shared._

import scala.collection.mutable.{ArrayBuffer, HashMap}
import scalafx.collections.{ObservableBuffer, ObservableHashMap}
import scalafx.scene.layout.VBox

object ClientControl {
  val socket:Socket = new Socket("localhost", 8008)
  var user:TwitterUser = null
  val users:ObservableBuffer[String] = new ObservableBuffer[String]()
  val globalChat:ObservableHashMap[Long, Message] = new ObservableHashMap[Long, Message]()
  val groupChat:ObservableHashMap[Long, Message] = new ObservableHashMap[Long, Message]()
  val statements:ObservableBuffer[Statement] = new ObservableBuffer[Statement]()
  val comments:HashMap[Long, ObservableBuffer[Comment]] = new HashMap[Long, ObservableBuffer[Comment]]()
  val polls: HashMap[Long, ObservableBuffer[Poll]] = new HashMap[Long, ObservableBuffer[Poll]]()

  val pollAnswers: ObservableHashMap[Poll, ArrayBuffer[PollAnswer]] = new ObservableHashMap[Poll, ArrayBuffer[PollAnswer]]()
  val chatRooms:ObservableBuffer[Statement] = new ObservableBuffer[Statement]()
  val activityFeed:ObservableBuffer[VBox] = new ObservableBuffer[VBox]()

  def setupClient(user1: TwitterUser): Unit ={
    ServerHandler.start()
    user = user1
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
    ClientMessageSender.writePoll(poll)
  }

  def subscribe(statement: Statement): Unit ={
    ClientMessageSender.writeSubscribe(statement)
  }

  /**
    * Returns a statement with the correct statementID if no statement has the ID it returns null
    * @param statementID statementID of the statement
    * @return correct statement or null
    */
  def getStatementFromID(statementID: Long):Statement={
    var statementReturn:Statement = null
    for(statement:Statement <- statements){
      if(statement.ID == statementID){
      statementReturn = statement
      }
    }
    statementReturn
  }

  def unsubscribe(statement: Statement): Unit ={
    ClientMessageSender.writeUnsubscribe(statement)
  }
}