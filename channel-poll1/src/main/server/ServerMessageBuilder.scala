package main.server

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
    Jmsg.put("type", JsonType.LOGINSUCCESS)
    Jmsg.put("name", nick)
  }

  def loginFailed(nick:String):JSONObject ={
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.LOGINFAILED.toString)
    Jmsg.put("reason", "nick already used")
  }

  def writeChatMessage(sender:String, msg:String, stamp:String): JSONObject ={
  writeGeneralMessage(sender, msg, stamp)
  }

  def writeGroupMessage(sender:String, msg:String, stamp:String, group:String): JSONObject={
    writeGeneralMessage(sender, msg, stamp).put("group", group)
  }

  def writeGeneralMessage(sender:String, msg:String, stamp:String): JSONObject ={
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.CHAT.toString)
    Jmsg.put("sender", sender)
    Jmsg.put("message", msg)
    Jmsg.put("stamp", stamp)
  }
}
