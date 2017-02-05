package client.model.clientCommunication

import main.shared.{ChatMessage, Message, Statement}
import org.json._
import main.shared.enums.JsonType

object ClientMessageBuilder {

  def loginMessage(nick: String): JSONObject = {
    val Jmsg: JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.LOGIN)
    Jmsg.put("name", nick)
  }

  def writeLogoutMessage(): JSONObject = {
    new JSONObject().put("type", JsonType.DISCONNECT)
  }

  def chatMessage(message: Message): JSONObject = {
    val Jmsg: JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.CHAT)
    Jmsg.put("senderID", message.sender)
    Jmsg.put("stamp", message.stamp);
    Jmsg.put("message", message.msg)
    Jmsg.put("recv", message.rcv)
  }


  /*
    def comment(comment: ChatMessage):JSONObject = {
      val Jmsg:JSONObject = new JSONObject()
      Jmsg.put("type", JsonType.COMMENT)
      Jmsg.put("senderID", comment.userID)
      Jmsg.put("stamp", comment.createdAt)
      Jmsg.put("message", comment.message)
      Jmsg.put("statementID", comment.statementID)
    }
    */
}
