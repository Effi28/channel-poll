package client.model.clientCommunication

import main.shared._
import org.json._
import main.shared.enums.JsonType

object ClientMessageBuilder {

  def writeLogin(nick: String): JSONObject = {
    val Jmsg: JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.LOGIN)
    Jmsg.put("name", nick)
  }


  def writeLogoutMessage(nick:String): JSONObject ={
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.DISCONNECT)
    Jmsg.put("name", nick)
  }

  def writeSubscribe(nick:String, statement: Statement): JSONObject ={
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.SUBSCRIBE)
    Jmsg.put("statementID", statement.ID)
    Jmsg.put("name", nick)
  }

  def writeUnsubscribe(nick:String, statement: Statement): JSONObject ={
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.UNSUBSCRIBE)
    Jmsg.put("statementID", statement.ID)
    Jmsg.put("name", nick)
  }

  def writeMessage(message: Message): JSONObject = {
    val Jmsg: JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.CHAT)
    Jmsg.put("senderID", message.sender)
    Jmsg.put("stamp", message.stamp);
    Jmsg.put("message", message.msg)
    Jmsg.put("recv", message.rcv)
  }


  def writeComment(comment: Comment):JSONObject = {
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.COMMENT)
    Jmsg.put("statementID", comment.statementID)
    Jmsg.put("senderID", comment.screenName)
    Jmsg.put("message", comment.message)
    Jmsg.put("id", comment.ID)
    Jmsg.put("stamp", comment.stamp)
  }

  def pollAnswer(pollAnswer: PollAnswer): JSONObject = {
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.POLLANSWER)
    Jmsg.put("userid", pollAnswer.userID)
    Jmsg.put("username", pollAnswer.userName)
    Jmsg.put("question", pollAnswer.question)
    Jmsg.put("selectedoptionkey", pollAnswer.selectedOption._1)
    Jmsg.put("selectedoptionstr", pollAnswer.selectedOption._2)
    Jmsg.put("pollid", pollAnswer.pollID)
    Jmsg.put("timestamp", pollAnswer.timestamp)
    Jmsg.put("statementid", pollAnswer.statementID)
  }

  def writePoll(poll: Poll): JSONObject = {
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.POLL)
    Jmsg.put("statementid", poll.statementID)
    Jmsg.put("stamp", poll.stamp)
    Jmsg.put("userid", poll.userID)
    Jmsg.put("username", poll.userName)
    Jmsg.put("question", poll.question)
    Jmsg.put("pollid", poll.pollID)
    val arr:JSONArray = new JSONArray()
    for ((k, v) <- poll.options) {
      val obj: JSONObject = new JSONObject()
      obj.put("key", k)
      obj.put("optionsstr",v._1)
      obj.put("likes", v._2)
      arr.put(obj)
    }
    Jmsg.put("options", arr)
  }

  def writeStatement(statement: Statement): JSONObject ={
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.STATEMENT)
    Jmsg.put("userid", statement.userID)
    Jmsg.put("message", statement.message)
    Jmsg.put("name", statement.userName)
    Jmsg.put("screenname", statement.screenName)
    Jmsg.put("pictureurl", statement.pictureURL)
    Jmsg.put("created_at", statement.creationDate)
    Jmsg.put("id", statement.ID)
  }
}
