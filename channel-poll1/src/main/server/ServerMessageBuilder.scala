package main.server

import main.shared.{Message, Statement}
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
    Jmsg.put("userid", statement.message)
    Jmsg.put("name", statement.userName)
    Jmsg.put("screenname", statement.screenName)
    Jmsg.put("pictureurl", statement.pictureURL)
    Jmsg.put("created_at", statement.creationDate)
    Jmsg.put("id", statement.ID)
  }
}
