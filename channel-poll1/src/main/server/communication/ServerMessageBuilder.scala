package main.server.communication

import main.server.JsonFiles.GetJsons
import main.shared.communication.MessageBuilder
import main.shared.enums.JsonType
import org.json.{JSONArray, JSONObject}

final object ServerMessageBuilder extends MessageBuilder{

  def loginSucceded(users: Iterable[String]): JSONObject = {
    val Jmsg: JSONObject = new JSONObject()
    val arr: JSONArray = new JSONArray()
    val sttmnts: JSONArray = GetJsons.getLastStatements()
    for (u <- users) arr.put(u)
    Jmsg.put("users", arr)
    Jmsg.put("statements", sttmnts)
    Jmsg.put("type", JsonType.LOGINSUCCESS)
  }

  def loginFailed(nick: String): JSONObject = {
    val Jmsg: JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.LOGINFAILED.toString)
    Jmsg.put("reason", "nick already used")
  }
}
