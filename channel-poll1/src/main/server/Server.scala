package main.server

import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.{ExecutorService, Executors}

import main.shared._

import scala.collection.mutable
import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer

object Server {
  val connectedHandler:HashMap[String, ClientHandler] = new HashMap[String, ClientHandler]
  val chatRooms:HashMap[ClientHandler, ArrayBuffer[Long]] = new HashMap[ClientHandler, ArrayBuffer[Long]];
  val statements:HashMap[Long, Statement] = new HashMap[Long, Statement]
  val comments:HashMap[Long, ArrayBuffer[Comment]] = new HashMap[Long, ArrayBuffer[Comment]]
  val polls:HashMap[Long, ArrayBuffer[Poll]] = new HashMap[Long, ArrayBuffer[Poll]]
  val pollAnswers:HashMap[Poll, ArrayBuffer[PollAnswer]] = new HashMap[Poll, ArrayBuffer[PollAnswer]]
  var chatID = 0

  def main(args: Array[String]): Unit = {
    startTwitterStream()

    val pool: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    val serverSocket = new ServerSocket(8008)
    try {
      while (true) {
      val socket:Socket = serverSocket.accept()
      pool.execute(new ClientHandler(socket))
      }
    }
    finally {
      pool.shutdown()
    }
  }

  def startTwitterStream(): Unit = {
    val accessStreamer = new TwitterAccess
    accessStreamer.streamingThread.start()

    // start dequeue thread for getting statements from queue
    val statementGetter = new QueueGetter
    statementGetter.dequeueThread.start()
  }

  def checkLogin(nick:String, client:ClientHandler): Unit ={
    if(connectedHandler.keySet.contains(nick)){
      client.sender.writeLoginFailed(nick)
    }
    else{
      connectedHandler += (nick -> client)
      broadcastLogin(client, nick)
    }
  }

  def broadcastLogin(client:ClientHandler, nick:String): Unit ={
    for ((k, v) <- connectedHandler) {
      if(k.equals(nick)){
        v.sender.writeLoginSuccess(connectedHandler.keys)
      }
      else{
        v.sender.writeNewLogin(nick)
      }
    }
  }

  def broadcastGlobalMessage(message:Message): Unit = {
    for ((k, v) <- connectedHandler) {
      if(!k.equals(message.sender)){
        v.sender.writeChatMessage(message)
      }
    }
    chatID = chatID + 1;
  }

  def broadcastGroupMessage(message:Message): Unit = {
  //TODO
  }

  def removeClient(clientHandler: ClientHandler): Unit ={
    var user:String = null
    for ((k, v) <- connectedHandler) {
      if(v.equals(clientHandler)){
        connectedHandler.remove(k)
        user = k
      }
  }
    for ((k, v) <- connectedHandler) {
      if(v.equals(clientHandler)){
        v.sender.writeDisconnect(user)
      }
    }
  }

  def removeClient(user:String): Unit ={
    connectedHandler.remove(user)
    for ((k, v) <- connectedHandler) {
      v.sender.writeDisconnect(user)
    }
  }

  def broadcastStatement(statement:Statement): Unit ={
    statements += statement.ID -> statement
    println(statement)
    for ((k, v) <- connectedHandler) {
      v.sender.writeStatement(statement)
    }
  }

  def broadcastComment(comment:Comment): Unit ={
    if(!comments.contains(comment.statementID)){
      comments += comment.statementID -> new ArrayBuffer[Comment]()

    }
    comments.get(comment.statementID).get += comment
    for ((k:String, v:ClientHandler) <- connectedHandler) {
      if(chatRooms.contains(v) && chatRooms.get(v).get.contains(comment.statementID)){
        v.sender.writeComment(comment)
      }
    }
  }

  def broadcastPoll(poll: Poll): Unit = {
    if(!polls.contains(poll.statementID)){
      polls += poll.statementID -> new ArrayBuffer[Poll]()
    }
    polls.get(poll.statementID).get += poll
    for ((k, v) <- connectedHandler) {
      if(chatRooms.contains(v) && chatRooms.get(v).get.contains(poll.statementID)){
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
    val polls_map: ArrayBuffer[Poll] = polls(pollAnswer.statementID)
    val thisPoll = polls_map(pollAnswer.pollID)
    pollAnswers.get(thisPoll).get += pollAnswer
    for ((k, v) <- connectedHandler) {
      v.sender.writePollAnswer(pollAnswer)
    }
  }

  /**
    * evaluates poll after each new selection
    * @param poll poll refering to a certain statement
    * @param selectedOption  option user selected in poll
    */
  def calcPoll(poll: Poll, selectedOption: (Int, String)): HashMap[Int, (String, Int)] ={
    val polOptions = poll.options
    val optionid = selectedOption._1
    val options_tuple = polOptions(optionid)

    polOptions.update(optionid, (selectedOption._2, options_tuple._2 + selectedOption._1)
    )
    return polOptions
  }

  def updatePoll(pollAnswer: PollAnswer): Unit = {
    val polls_map: ArrayBuffer[Poll] = polls(pollAnswer.statementID)
    val thisPoll = polls_map(pollAnswer.pollID)
    val newOptions = calcPoll(thisPoll, pollAnswer.selectedOption)
    val updatedPoll = new Poll(thisPoll.pollID, thisPoll.statementID, thisPoll.stamp, thisPoll.pollID, thisPoll.userName, thisPoll.question,
      newOptions)
    polls_map(thisPoll.pollID) = updatedPoll
    polls.update(pollAnswer.statementID, polls_map)
    broadcastPollUpdate(updatedPoll)
  }
}