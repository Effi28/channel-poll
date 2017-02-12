package main.client.model

import java.net.Socket

import main.shared._
import main.shared.data._

import scala.collection.mutable.{ArrayBuffer, HashMap}
import scalafx.collections.{ObservableBuffer, ObservableHashMap}
import scalafx.scene.layout.VBox

final object ClientControl {
  val socket: Socket = new Socket("localhost", 8008)
  var user: TwitterUser = null
  val users: ObservableBuffer[TwitterUser] = new ObservableBuffer[TwitterUser]()
  val statements: ObservableBuffer[Statement] = new ObservableBuffer[Statement]()
  val comments: HashMap[Long, ObservableBuffer[Comment]] = new HashMap[Long, ObservableBuffer[Comment]]()
  val polls: HashMap[Long, ObservableBuffer[Poll]] = new HashMap[Long, ObservableBuffer[Poll]]()
  val chatRooms: ObservableBuffer[Statement] = new ObservableBuffer[Statement]()
  val activityFeed: ObservableBuffer[VBox] = new ObservableBuffer[VBox]()

  def setupClient(user1: TwitterUser): Unit = {
    ServerHandler.start()
    user = user1
    ClientMessageSender.writeLoginMessage(user)
  }

  def sendComment(comment: Comment): Unit = {
    ClientMessageSender.writeStComment(comment)
  }

  def sendPoll(poll: Poll): Unit = {
    ClientMessageSender.writePoll(poll)
  }

  def sendPollAnswer(pollAnswer: PollAnswer):Unit={
    ClientMessageSender.writePollAnswer(pollAnswer)
  }

  def logout(): Unit = {
    ClientMessageSender.writeLogout(user)
  }

  def close() = {
    System.exit(0)
  }

  def subscribe(statement: Statement): Unit = {
    ClientMessageSender.writeSubscribe(statement)
  }

  def unsubscribe(statement: Statement): Unit = {
    ClientMessageSender.writeUnsubscribe(statement)
  }
}