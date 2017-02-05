package client.model.clientCommunication

import main.shared._
import org.json._
import main.shared.enums.JsonType

object ClientMessageBuilder {

  def loginMessage(nick: String): JSONObject = {
    val Jmsg: JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.LOGIN)
    Jmsg.put("name", nick)
  }


  def writeLogoutMessage(nick:String): JSONObject ={
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.DISCONNECT)
    Jmsg.put("name", nick)
  }

  def chatMessage(message: Message): JSONObject = {
    val Jmsg: JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.CHAT)
    Jmsg.put("senderID", message.sender)
    Jmsg.put("stamp", message.stamp);
    Jmsg.put("message", message.msg)
    Jmsg.put("recv", message.rcv)
  }


  def comment(comment: Comment):JSONObject = {
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.COMMENT)
    Jmsg.put("statement", writeStatement(comment.statement))
    Jmsg.put("senderID", comment.screenName)
    Jmsg.put("message", comment.message)
    Jmsg.put("id", comment.ID)
    Jmsg.put("stamp", comment.stamp)
  }

  def pollAnswer(pollAnswer: PollAnswer): JSONObject = {
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

  def poll(poll: Poll): JSONObject = {
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
