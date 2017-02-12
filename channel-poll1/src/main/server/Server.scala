package main.server

import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.{ExecutorService, Executors}

import main.server.JsonFiles.SaveJsons
import main.server.communication.ClientHandler
import main.server.twitter.{QueueGetter, TwitterAccess}
import main.shared.data.{Comment, Poll, PollAnswer, Statement}

import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer

final object Server {
  private val connectedHandler: HashMap[String, ClientHandler] = new HashMap[String, ClientHandler]
  private val chatRooms: HashMap[ClientHandler, ArrayBuffer[Long]] = new HashMap[ClientHandler, ArrayBuffer[Long]];
  private val statements: HashMap[Long, Statement] = new HashMap[Long, Statement]
  private val comments: HashMap[Long, ArrayBuffer[Comment]] = new HashMap[Long, ArrayBuffer[Comment]]
  private val polls: HashMap[Long, ArrayBuffer[Poll]] = new HashMap[Long, ArrayBuffer[Poll]]
  private val pollAnswers: HashMap[Long, ArrayBuffer[PollAnswer]] = new HashMap[Long, ArrayBuffer[PollAnswer]]
  private var chatID = 0

  def main(args: Array[String]): Unit = {
    startTwitterStream()
    startSaveJson()
    val pool: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    val serverSocket = new ServerSocket(8008)
    try {
      while (true) {
        val socket: Socket = serverSocket.accept()
        pool.execute(new ClientHandler(socket))
      }
    }
    finally {
      pool.shutdown()
    }
  }

  def handleSubscribe(statementID: Long, nick: String): Unit = {
    val tempClient: ClientHandler = Server.connectedHandler(nick)
    if (!Server.chatRooms.contains(tempClient)) {
      Server.chatRooms += tempClient -> new ArrayBuffer[Long]
    }
    Server.chatRooms.get(tempClient).get += statementID
  }

  def handleUnSubscribe(statementID: Long, nick: String): Unit = {
    val tempClient: ClientHandler = Server.connectedHandler(nick)
    Server.chatRooms.get(tempClient).get -= statementID
    if (Server.chatRooms.get(tempClient).size == 0) {
      Server.chatRooms -= tempClient
    }
  }

  private def startTwitterStream(): Unit = {
    TwitterAccess.streamingThread.start()
    // start dequeue thread for getting statements from queue
    QueueGetter.dequeueThread.start()
  }

  private def startSaveJson(): Unit = {
    SaveJsons.saveJsonsThread.start()
  }

  def checkLogin(nick: String, client: ClientHandler): Unit = {
    if (connectedHandler.keySet.contains(nick)) {
      client.sender.writeLoginFailed(nick)
    }
    else {
      connectedHandler += (nick -> client)
      broadcastLogin(client, nick)
    }
  }

  private def broadcastLogin(client: ClientHandler, nick: String): Unit = {
    for ((k, v) <- connectedHandler) {


      if (k.equals(nick)) {
        v.sender.writeLoginSuccess(connectedHandler.keys)
      }
      else {
        v.sender.writeNewLogin(nick)
      }
    }
  }

  def removeClient(clientHandler: ClientHandler): Unit = {
    var user: String = null
    for ((k, v) <- connectedHandler) {
      if (v.equals(clientHandler)) {
        connectedHandler.remove(k)
        user = k
      }
    }
    for ((k, v) <- connectedHandler) {
      if (v.equals(clientHandler)) {
        v.sender.writeDisconnect(user)
      }
    }
  }

  def removeClient(user: String): Unit = {
    connectedHandler.remove(user)
    for ((k, v) <- connectedHandler) {
      v.sender.writeDisconnect(user)
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
      comments += comment.statementID -> new ArrayBuffer[Comment]()
    }
    comments.get(comment.statementID).get += comment
    for ((k: String, v: ClientHandler) <- connectedHandler) {
      if (chatRooms.contains(v) && chatRooms.get(v).get.contains(comment.statementID)) {
        v.sender.writeComment(comment)
      }
    }
  }

  def broadcastPoll(poll: Poll): Unit = {
    if (!polls.contains(poll.statementID)) {
      polls += poll.statementID -> new ArrayBuffer[Poll]()
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
    if (!pollAnswers.contains(pollAnswer.pollID)) {
      pollAnswers += pollAnswer.pollID -> new ArrayBuffer[PollAnswer]()
    }
    pollAnswers.get(pollAnswer.pollID).get += pollAnswer
    for ((k, v) <- connectedHandler) {
      if (chatRooms.contains(v) && chatRooms.get(v).get.contains(pollAnswer.statementID)) {
        v.sender.writePollAnswer(pollAnswer)
      }
    }
  }

  /**
    * evaluates poll after each new selection
    *
    * @param poll           poll refering to a certain statement
    * @param selectedOption option user selected in poll
    */
  def calcPoll(poll: Poll, selectedOption: (Int, String)): HashMap[Int, (String, Int)] = {
    val polOptions = poll.options
    val optionid = selectedOption._1
    val options_tuple = polOptions(optionid)
    polOptions.update(optionid, (selectedOption._2, options_tuple._2 + selectedOption._1))
    return polOptions
  }

  def updatePoll(pollAnswer: PollAnswer): Unit = {
    val polls_map: ArrayBuffer[Poll] = polls(pollAnswer.statementID)
    val thisPoll = polls_map(pollAnswer.pollID.toInt)
    val newOptions = calcPoll(thisPoll, pollAnswer.selectedOption)
    val updatedPoll = new Poll(thisPoll.ID, thisPoll.statementID, thisPoll.userID, thisPoll.userName, thisPoll.question,
      newOptions, thisPoll.timestamp)
    polls_map(thisPoll.ID.toInt) = updatedPoll
    polls.update(pollAnswer.statementID, polls_map)
    broadcastPollUpdate(updatedPoll)
  }
}