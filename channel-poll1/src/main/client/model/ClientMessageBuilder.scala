package main.client.model

import main.shared.communication.MessageBuilder
import main.shared.data.{Statement, TwitterUser}
import main.shared.enums.JsonType
import org.json._
import twitter4j.Twitter

final object ClientMessageBuilder extends MessageBuilder{

  def writeSubscribe(user: TwitterUser, statement: Statement): JSONObject = {
    val Jmsg: JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.SUBSCRIBE)
    Jmsg.put("statementid", statement.ID)
    Jmsg.put("username", user.userName)
    Jmsg.put("userid", user.ID)
  }

  def writeUnsubscribe(user: TwitterUser, statement: Statement): JSONObject = {
    val Jmsg: JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.UNSUBSCRIBE)
    Jmsg.put("statementid", statement.ID)
    Jmsg.put("username", user.userName)
    Jmsg.put("userid", user.ID)
  }
}
