package main.client.model

import main.shared.communication.MessageBuilder
import main.shared.data.{Statement}
import main.shared.enums.JsonType
import org.json._

final object ClientMessageBuilder extends MessageBuilder{

  def writeSubscribe(nick: String, statement: Statement): JSONObject = {
    val Jmsg: JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.SUBSCRIBE)
    Jmsg.put("statementid", statement.ID)
    Jmsg.put("username", nick)
  }

  def writeUnsubscribe(nick: String, statement: Statement): JSONObject = {
    val Jmsg: JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.UNSUBSCRIBE)
    Jmsg.put("statementid", statement.ID)
    Jmsg.put("username", nick)
  }
}
