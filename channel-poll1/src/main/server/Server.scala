package main.server

import java.net.ServerSocket
import java.util.concurrent.Executors
import main.server.JsonFiles.SaveJsons
import main.server.communication.ClientHandler
import main.server.twitter.{QueueGetter, TwitterAccess}
import main.shared.data._
import main.shared.data.Serializable
import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer

final private object Server {
  private val connectedHandler= new HashMap[TwitterUser, ClientHandler]
  private val chatRooms = new HashMap[ClientHandler, ArrayBuffer[Long]];
  private val statements = new HashMap[Long, Statement]
  private val comments = new HashMap[Long, ArrayBuffer[Comment]]
  private val polls = new HashMap[Long, ArrayBuffer[Poll]]

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

  private def startTwitterStream = {
    TwitterAccess.streamingThread.start
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

  def +=(statement: Statement): Unit = {
    statements += statement.ID -> statement
   broadcast(statement)
  }

  def +=(comment: Comment): Unit = {
    if (!comments.contains(comment.statementID)) {
      comments += comment.statementID -> ArrayBuffer[Comment]()
    }
    comments.get(comment.statementID).get += comment
    broadcast(comment)
  }

  def +=(poll: Poll): Unit = {
    if (!polls.contains(poll.statementID)) {
      polls += poll.statementID -> ArrayBuffer[Poll]()
    }
    polls.get(poll.statementID).get += poll
    broadcast(poll)
  }

  def +=(pollAnswer: PollAnswer): Unit = {
    if(!polls.contains(pollAnswer.statementID)){
      polls += pollAnswer.statementID -> new ArrayBuffer[Poll]
    }
    for(poll <- polls.get(pollAnswer.statementID).get){
      if(poll.ID == pollAnswer.pollID){
        for(option <- poll.options){
          if(option.key == pollAnswer.selectedOption._1){
            if(!option.likeUser.contains(pollAnswer.userID)) {option.likeUser += pollAnswer.userName}
            option.likes += 1
          }
        }
      }
    }
    broadcast(pollAnswer)
  }

  def broadcast(serializable: Serializable): Unit ={
    for ((k, v) <- connectedHandler) {
      v.sender.writeData(serializable)
    }
  }
}