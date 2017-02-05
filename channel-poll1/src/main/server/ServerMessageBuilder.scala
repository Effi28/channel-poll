package main.server

import java.security.Timestamp

import main.shared._
import main.shared.enums.JsonType
import org.json.JSONObject
import org.json.JSONArray

object ServerMessageBuilder {

  def loginSucceded(users:Iterable[String]): JSONObject ={
    val Jmsg:JSONObject = new JSONObject()
    val arr:JSONArray = new JSONArray()
    for (u <- users) arr.put(u)
    Jmsg.put("users", arr)
    Jmsg.put("type", JsonType.LOGINSUCCESS)
  }

  def newLogin(nick:String): JSONObject ={
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.LOGIN)
    Jmsg.put("name", nick)
  }

  def loginFailed(nick:String):JSONObject ={
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.LOGINFAILED.toString)
    Jmsg.put("reason", "nick already used")
  }

  def userDisconnect(nick:String):JSONObject ={
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.DISCONNECT)
    Jmsg.put("name", nick)
  }

  def writeChatMessage(message:Message): JSONObject ={
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.CHAT.toString)
    Jmsg.put("sender", message.sender)
    Jmsg.put("message", message.msg)
    Jmsg.put("stamp", message.stamp)
    Jmsg.put("recv", message.rcv)
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


  def writeComment(comment: Comment): JSONObject = {
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.COMMENT)
    Jmsg.put("senderID", comment.screenname)
    Jmsg.put("message", comment.message)
    Jmsg.put("statementID", comment.ID)
  }

  def writePoll(poll: Poll): JSONObject = {
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.POLL)
    Jmsg.put("statementid", poll.statementID)
    Jmsg.put("stamp", poll.stamp)
    Jmsg.put("user", poll.user)
    Jmsg.put("question", poll.question)

    val Jmsg1:JSONArray = new JSONArray()
    for ((k, v) <- poll.options) {
      val Jmsg2: JSONObject = new JSONObject()
      Jmsg2.put("optionsstr", v._1)
      Jmsg2.put("likes", v._2)
      Jmsg2.put("key", Jmsg2)
      Jmsg1.put(Jmsg2)
    }
    Jmsg.put("options", Jmsg1)
    Jmsg.put("pollid", poll.pollID)
  }

  def writePollAnswer(pollAnswer: PollAnswer): JSONObject = {
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.POLLANSWER)
    Jmsg.put("userid", pollAnswer.userid)
    Jmsg.put("question", pollAnswer.question)
    Jmsg.put("selectedoptionkey", pollAnswer.selectedOption._1)
    Jmsg.put("selectedoptionstr", pollAnswer.selectedOption._2)
    Jmsg.put("pollid", pollAnswer.pollID)
    Jmsg.put("timestamp", pollAnswer.timestamp)
    Jmsg.put("statementid", pollAnswer.statementID)
  }

}
