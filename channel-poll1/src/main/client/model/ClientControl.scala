package main.client.model

import java.net.Socket
import main.shared.data._
import scala.collection.mutable.HashMap
import scalafx.collections.ObservableBuffer

final object ClientControl {

  import scala.collection.mutable.ArrayBuffer

  val socket: Socket = new Socket("localhost", 8008)
  var user: TwitterUser = null
  val users: ObservableBuffer[TwitterUser] = new ObservableBuffer[TwitterUser]()
  val statements: ObservableBuffer[Statement] = new ObservableBuffer[Statement]()
  val comments: HashMap[Long, ObservableBuffer[Comment]] = new HashMap[Long, ObservableBuffer[Comment]]()
  val polls: HashMap[Long, ObservableBuffer[Poll]] = new HashMap[Long, ObservableBuffer[Poll]]()
  //val chatRooms: ObservableBuffer[Statement] = new ObservableBuffer[Statement]()
  val chatRooms: HashMap[Statement, ObservableBuffer[TwitterUser]] = new HashMap[Statement, ObservableBuffer[TwitterUser]]()
  val userChatRooms: HashMap[TwitterUser, ObservableBuffer[Statement]] = new HashMap[TwitterUser, ObservableBuffer[Statement]]()

  def setupClient(user1: TwitterUser): Unit = {
    listen().start()
    user = user1
    if (!userChatRooms.contains(user1)) {
      userChatRooms.put(user1, new ObservableBuffer[Statement]())
    }
    ClientMessageSender.writeLoginMessage(user)
  }

  def listen(): Thread = {
    new Thread(() => {
      while (true) {
        ClientMessageReceiver.readMessage()
      }
    })
  }

  def +=(poll: Poll): Unit = {
    if (!ClientControl.polls.contains(poll.statementID)) {
      ClientControl.polls += poll.statementID -> new ObservableBuffer[Poll]

    }
    ClientControl.polls.get(poll.statementID).get += poll
  }

  def +=(pollAnswer: PollAnswer): Unit = {
    for (poll <- ClientControl.polls.get(pollAnswer.statementID).get) {
      if (poll.ID == pollAnswer.pollID) {
        for (option <- poll.options) {
          if (option.key == pollAnswer.selectedOption._1) {
            option.likes += 1
            println(option.name + " has: " + option.likes + " likes.")
          }
        }
      }
    }
  }

  def +=(comment: Comment): Unit = {
    if (!ClientControl.comments.contains(comment.statementID)) {
      ClientControl.comments += comment.statementID -> new ObservableBuffer[Comment]()
    }
    ClientControl.comments.get(comment.statementID).get += comment
  }

  def sendComment(comment: Comment): Unit = {
    ClientMessageSender.writeStComment(comment)
  }

  def sendPoll(poll: Poll): Unit = {
    ClientMessageSender.writePoll(poll)
  }

  def sendPollAnswer(pollAnswer: PollAnswer): Unit = {
    ClientMessageSender.writePollAnswer(pollAnswer)
  }

  def logout(): Unit = {
    ClientMessageSender.writeLogout(user)
  }

  def subscribe(statement: Statement, subscribe: Boolean): Unit = {

    if (subscribe) {
      if (!ClientControl.userChatRooms.get(user).contains(statement)) {
        ClientControl.userChatRooms.get(user).get += statement
      }
      if (!ClientControl.chatRooms.get(statement).contains(user)) {
        ClientControl.chatRooms.get(statement).get += user
      }
    }

    else {
      if (ClientControl.userChatRooms.get(user).contains(statement)) {
        ClientControl.userChatRooms.get(user).get -= statement
      }
      if (ClientControl.chatRooms.get(statement).contains(user)) {
        ClientControl.chatRooms.get(statement).get -= user
      }

    }
    ClientMessageSender.writeSubscribe(statement, subscribe)
  }


  def close() = {
    System.exit(0)
  }
}