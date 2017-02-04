package client.model.clientCommunication

import main.shared.{Comment, Message, Statement}
import org.json._
import main.shared.enums.JsonType

object ClientMessageBuilder {

  def loginMessage(nick:String): JSONObject = {
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.LOGIN)
    Jmsg.put("name", nick)
  }

  def writeLogoutMessage(nick:String): JSONObject ={
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.DISCONNECT)
    Jmsg.put("name", nick)
  }

  def chatMessage(message:Message):JSONObject = {
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.CHAT)
    Jmsg.put("senderID", message.sender)
    Jmsg.put("stamp", message.rcv);
    Jmsg.put("message", message.msg)
    Jmsg.put("recv", message.rcv)
  }

  def comment(comment: Comment):JSONObject = {
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.COMMENT)
    Jmsg.put("statement", writeStatement(comment.statement))
    Jmsg.put("senderID", comment.screenname)
    Jmsg.put("message", comment.message)
    Jmsg.put("likes", comment.likes)
    Jmsg.put("id", comment.ID)
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
