package main.client.model

import main.shared.communication.MessageBuilder
import main.shared.data.{Statement, TwitterUser}
import main.shared.enums.JsonType
import org.json._

final object ClientMessageBuilder extends MessageBuilder{
  def writeSubscribe(user: TwitterUser, statement: Statement, subscribe:Boolean) = {
    val Jmsg = new JSONObject
    if(subscribe) Jmsg.put("type", JsonType.SUBSCRIBE) else Jmsg.put("type", JsonType.UNSUBSCRIBE)
    Jmsg.put("statementid", statement.ID)
    Jmsg.put("username", user.userName)
    Jmsg.put("userid", user.ID)
  }
}
