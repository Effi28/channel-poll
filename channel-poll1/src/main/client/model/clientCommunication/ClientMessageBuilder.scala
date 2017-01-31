package client.model.clientCommunication

import main.shared.{Message, Statement}
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

  def chatMessage(message:Message):JSONObject = {
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.CHAT)
    Jmsg.put("senderID", message.sender)
    Jmsg.put("stamp", message.rcv);
    Jmsg.put("message", message.msg)
    Jmsg.put("recv", message.rcv)
  }
}
