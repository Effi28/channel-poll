package main.server.communication

import java.io.BufferedReader
import main.shared.enums.JsonType
import main.shared.enums.JsonType.JsonType
import org.json.JSONObject
import org.slf4j.{LoggerFactory}
import main.shared.communication.MessageReceiver
import main.shared.data.TwitterUser

class ServerMessageReceiver(in: BufferedReader, client: ClientHandler) extends MessageReceiver{
  private val logger = LoggerFactory.getLogger(this.getClass)

  def readMessage = {
    while (!client.socket.isClosed) {
      val jsonText = in.readLine
      if (jsonText != null) {
        val jsonObject = new JSONObject(jsonText)
        logger.info(jsonObject.toString)
        matchTest(JsonType.withName(jsonObject.optString("type")), jsonObject)
      }
    }
  }

  private def matchTest(x: JsonType, jSONObject: JSONObject): Unit = x match {
    case JsonType.LOGIN => client.handleLogin(login(jSONObject))
    case JsonType.DISCONNECT => client.handleLogout(logout(jSONObject))
    case JsonType.STATEMENT => client.handleStatement(statement(jSONObject))
    case JsonType.COMMENT => client.handleComment(comment(jSONObject))
    case JsonType.POLL => client.handlePoll(poll(jSONObject))
    case JsonType.POLLANSWER => client.handlePollAnswer(pollAnswer(jSONObject))
    case _ => invalid(jSONObject)
  }
}
