package main.server.communication

import main.server.JsonFiles.GetJsons
import main.shared.communication.MessageBuilder
import main.shared.data.{Statement, TwitterUser}
import main.shared.enums.JsonType
import org.json.{JSONArray, JSONObject}

final object ServerMessageBuilder extends MessageBuilder{

  def writeStatement(statement: Statement): JSONObject = {
    val Jmsg = new JSONObject()
    Jmsg.put("type", JsonType.STATEMENT)
    Jmsg.put("id", statement.ID)
    Jmsg.put("userid", statement.userID)
    Jmsg.put("username", statement.userName)
    Jmsg.put("pictureurl", statement.pictureURL)
    Jmsg.put("message", statement.message)
    Jmsg.put("timestamp", statement.timestamp)
  }

  def loginSucceded(users: Iterable[TwitterUser]): JSONObject = {
    val Jmsg = new JSONObject()
    val arr = new JSONArray()
    val sttmnts = GetJsons.getLastStatements()
    val cmnts = GetJsons.getLastCPP("Comment")
    val plls = GetJsons.getLastCPP("Poll")
    val pllnswrs = GetJsons.getLastCPP("PollAnswer")
    for (u <- users) {
      val jsonUser:JSONObject = new JSONObject()
      jsonUser.put("username", u.userName)
      jsonUser.put("userid", u.ID)
      arr.put(jsonUser)
    }
    Jmsg.put("users", arr)
    Jmsg.put("polls", plls)
    Jmsg.put("pollanswers", pllnswrs)
    Jmsg.put("statements", sttmnts)
    Jmsg.put("comments", cmnts)
    Jmsg.put("type", JsonType.LOGINSUCCESS)
  }

  def loginFailed(user: TwitterUser): JSONObject = {
    val Jmsg = new JSONObject()
    Jmsg.put("type", JsonType.LOGINFAILED.toString)
    Jmsg.put("username", user.userName)
    Jmsg.put("userid", user.ID)
    Jmsg.put("reason", "nick already used")
  }
}
