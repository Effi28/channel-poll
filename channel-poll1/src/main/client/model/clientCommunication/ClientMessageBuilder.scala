package client.model.clientCommunication

import org.json._
import main.shared.enums.JsonType

object ClientMessageBuilder {

  def loginMessage(nick:String): JSONObject = {
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.LOGIN)
    Jmsg.put("name", nick)
  }

  def writeLogoutMessage(): JSONObject ={
    new JSONObject().put("type", JsonType.DISCONNECT)
  }

  def chatMessage(senderID:String, msg:String):JSONObject = {
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.CHAT)
    Jmsg.put("senderID", senderID)
    Jmsg.put("stamp", System.currentTimeMillis());
    Jmsg.put("message", msg)
  }

  def writeChatMessage(senderID:String, msg:String):JSONObject = { chatMessage(senderID, msg) }

  def writeChatMessage(senderID:String, msg:String, receiverID:String): JSONObject ={
    chatMessage(senderID, msg).put("groupID", receiverID)
  }
}
