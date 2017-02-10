package main.client.model

import main.shared._
import main.shared.data.{Comment, Poll, PollAnswer, Statement}
import main.shared.enums.JsonType
import org.json._

final object ClientMessageBuilder {

  def writeLogin(nick: String): JSONObject = {
    val Jmsg: JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.LOGIN)
    Jmsg.put("username", nick)
  }


  def writeLogoutMessage(nick:String): JSONObject ={
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.DISCONNECT)
    Jmsg.put("username", nick)
  }

  def writeSubscribe(nick:String, statement: Statement): JSONObject ={
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.SUBSCRIBE)
    Jmsg.put("statementid", statement.ID)
    Jmsg.put("username", nick)
  }

  def writeUnsubscribe(nick:String, statement: Statement): JSONObject ={
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.UNSUBSCRIBE)
    Jmsg.put("statementid", statement.ID)
    Jmsg.put("username", nick)
  }

  def writeComment(comment: Comment):JSONObject = {
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.COMMENT)
    Jmsg.put("id", comment.ID)
    Jmsg.put("statementid", comment.statementID)
    Jmsg.put("userid", comment.userID)
    Jmsg.put("username", comment.userName)
    Jmsg.put("message", comment.message)
    Jmsg.put("timestamp", comment.timestamp)
  }

  def pollAnswer(pollAnswer: PollAnswer): JSONObject = {
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.POLLANSWER)
    Jmsg.put("pollid", pollAnswer.pollID)
    Jmsg.put("statementid", pollAnswer.statementID)
    Jmsg.put("userid", pollAnswer.userID)
    Jmsg.put("username", pollAnswer.userName)
    Jmsg.put("question", pollAnswer.question)
    Jmsg.put("selectedoptionkey", pollAnswer.selectedOption._1)
    Jmsg.put("selectedoptionstr", pollAnswer.selectedOption._2)
    Jmsg.put("timestamp", pollAnswer.timestamp)
  }

  def writePoll(poll: Poll): JSONObject = {
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.POLL)
    Jmsg.put("id", poll.ID)
    Jmsg.put("statementid", poll.statementID)
    Jmsg.put("userid", poll.userID)
    Jmsg.put("username", poll.userName)
    Jmsg.put("question", poll.question)
    val arr:JSONArray = new JSONArray()
    for ((k, v) <- poll.options) {
      val obj: JSONObject = new JSONObject()
      obj.put("key", k)
      obj.put("optionsstr",v._1)
      obj.put("likes", v._2)
      arr.put(obj)
    }
    Jmsg.put("options", arr)
    Jmsg.put("timestamp", poll.timestamp)
  }

  def writeStatement(statement: Statement): JSONObject ={
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.STATEMENT)
    Jmsg.put("id", statement.ID)
    Jmsg.put("userid", statement.userID)
    Jmsg.put("username", statement.userName)
    Jmsg.put("pictureurl", statement.pictureURL)
    Jmsg.put("message", statement.message)
    Jmsg.put("timestamp", statement.timestamp)
  }
}
