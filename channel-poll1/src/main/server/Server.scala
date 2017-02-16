package main.server

import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.{ExecutorService, Executors}

import main.server.JsonFiles.SaveJsons
import main.server.communication.ClientHandler
import main.server.twitter.{QueueGetter, TwitterAccess}
import main.shared.data._

import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer

final object Server {
  private val connectedHandler= new HashMap[TwitterUser, ClientHandler]
  private val chatRooms = new HashMap[ClientHandler, ArrayBuffer[Long]];
  private val statements = new HashMap[Long, Statement]
  private val comments = new HashMap[Long, ArrayBuffer[Comment]]
  private val polls = new HashMap[Long, ArrayBuffer[Poll]]
  private var chatID = 0

  def main(args: Array[String]) = {
    startTwitterStream
    startSaveJson
    val pool = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors)
    val serverSocket = new ServerSocket(8008)
    while (!serverSocket.isClosed) {
      pool.execute(ClientHandler(serverSocket.accept))
    }
    pool.shutdown
  }

  def handleSubscribe(statementID: Long, user: TwitterUser): Unit = {
    var tempClient:ClientHandler = null
    for ((k, v) <- connectedHandler) {
      if(k.ID == user.ID){
        tempClient = v
      }
    }
    if (!Server.chatRooms.contains(tempClient)) {
      Server.chatRooms += tempClient -> ArrayBuffer[Long]()
    }
    Server.chatRooms.get(tempClient).get += statementID
  }

  def handleUnSubscribe(statementID: Long, user: TwitterUser): Unit = {
    var tempClient:ClientHandler = null
    for ((k, v) <- connectedHandler) {
      if(k.ID == user.ID){
        tempClient = v
      }
    }
    Server.chatRooms.get(tempClient).get -= statementID
    if (Server.chatRooms.get(tempClient).size == 0) {
      Server.chatRooms -= tempClient
    }
  }

  private def startTwitterStream = {
    TwitterAccess.streamingThread.start
    // start dequeue thread for getting statements from queue
    QueueGetter.dequeueThread.start
  }

  private def startSaveJson = SaveJsons.saveJsonsThread.start

  def checkLogin(user: TwitterUser, client: ClientHandler): Unit = {
    if (connectedHandler.keySet.contains(user)) {
      client.sender.writeLoginFailed(user)
    }
    else {
      connectedHandler += (user -> client)
      broadcastLogin(client, user)
    }
  }

  private def broadcastLogin(client: ClientHandler, user: TwitterUser): Unit = {
    for ((k, v) <- connectedHandler) {
      if (k.equals(user)) {
        v.sender.writeLoginSuccess(connectedHandler.keys)
      }
      else {
        v.sender.writeLoginMessage(user)
      }
    }
  }

  def removeClient(clientHandler: ClientHandler): Unit = {
    var user: TwitterUser = null
    for ((k, v) <- connectedHandler) {
      if (v.equals(clientHandler)) {
        connectedHandler.remove(k)
        user = k
      }
    }
    for ((k, v) <- connectedHandler) {
        v.sender.writeLogout(user)
    }
  }

  def broadcastStatement(statement: Statement): Unit = {
    statements += statement.ID -> statement
    for ((k, v) <- connectedHandler) {
      v.sender.writeStatement(statement)
    }
  }

  def broadcastComment(comment: Comment): Unit = {
    if (!comments.contains(comment.statementID)) {
      comments += comment.statementID -> ArrayBuffer[Comment]()
    }
    comments.get(comment.statementID).get += comment
    for ((k: TwitterUser, v: ClientHandler) <- connectedHandler) {
      if (chatRooms.contains(v) && chatRooms.get(v).get.contains(comment.statementID)) {
        v.sender.writeStComment(comment)
      }
    }
  }

  def broadcastPoll(poll: Poll): Unit = {
    if (!polls.contains(poll.statementID)) {
      polls += poll.statementID -> ArrayBuffer[Poll]()
    }
    polls.get(poll.statementID).get += poll
    for ((k, v) <- connectedHandler) {
      if (chatRooms.contains(v) && chatRooms.get(v).get.contains(poll.statementID)) {
        v.sender.writePoll(poll)
      }
    }
  }

  def broadcastPollUpdate(poll: Poll): Unit = {
    for ((k, v) <- connectedHandler) {
      v.sender.writePoll(poll)
    }
  }

  def broadcastPollAnswers(pollAnswer: PollAnswer): Unit = {
    for(poll <- polls.get(pollAnswer.statementID).get){
      if(poll.ID == pollAnswer.pollID){
        for(option <- poll.options){
          if(option.key == pollAnswer.selectedOption._1){
            option.likes += 1
            println (option.name +" has: "  + option.likes + " likes.")
          }
        }
      }
    }

    for ((k, v) <- connectedHandler) {
      if (chatRooms.contains(v) && chatRooms.get(v).get.contains(pollAnswer.statementID)) {
        v.sender.writePollAnswer(pollAnswer)
      }
    }
  }
}