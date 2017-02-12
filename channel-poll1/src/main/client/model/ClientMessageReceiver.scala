package main.client.model

import java.io.{BufferedReader, InputStreamReader}

import main.client.controller.Controller
import main.shared.data._
import main.shared.communication.MessageReceiver
import main.shared.enums.JsonType
import org.json.{JSONArray, JSONObject}
import org.slf4j.{Logger, LoggerFactory}

final object ClientMessageReceiver extends MessageReceiver{
  private val in: BufferedReader = new BufferedReader(new InputStreamReader(ClientControl.socket.getInputStream, "UTF-8"))
  private val logger: Logger = LoggerFactory.getLogger(ClientMessageReceiver.getClass)

  def readMessage(): Unit = {
    var jsonText: String = null
    while (!ClientControl.socket.isClosed) {
      jsonText = in.readLine()
      if (jsonText != null) {
        val jsonObject: JSONObject = new JSONObject(jsonText)
        logger.info(jsonObject.toString())
        matchTest(JsonType.withName(jsonObject.optString("type")), jsonObject)
      }
    }
  }

  private def matchTest(x: JsonType.JsonType, jSONObject: JSONObject): Unit = x match {
    case JsonType.LOGINSUCCESS => handleLoginSuccessful(jSONObject)
    case JsonType.LOGINFAILED => handleLoginFailed(jSONObject)
    case JsonType.LOGIN => ServerHandler.handleLogin(login(jSONObject))
    case JsonType.DISCONNECT => ServerHandler.handleLogin(logout(jSONObject))
    case JsonType.STATEMENT => ServerHandler.handleStatement(statement(jSONObject))
    case JsonType.POLL => ServerHandler.handlePoll(poll(jSONObject))
    case JsonType.POLLANSWER => ServerHandler.handlePollAnswer(pollAnswer(jSONObject))
    case JsonType.COMMENT => ServerHandler.handleComment(comment(jSONObject))
    case _ => invalid(jSONObject)
  }

  private def handleLoginSuccessful(json: JSONObject): Unit = {
    val userNames: JSONArray = json.optJSONArray("users")
    for (i <- 0 until userNames.length()) {
      val userID:Long =  userNames.getJSONObject(i).optLong("userid")
      val userName:String = userNames.getJSONObject(i).optString("username")
      ServerHandler.handleLogin(new TwitterUser(userID, userName))
    }
    val reloadedStatements: JSONArray = json.optJSONArray("statements")

    for (j <- 0 until reloadedStatements.length()) {
      statement(reloadedStatements.getJSONObject(j))
    }
    Controller.exitLoginView()
  }

  private def handleLoginFailed(json: JSONObject): Unit = {
    //TODO show the reason why login failed(e.G. username already taken)
  }
}
