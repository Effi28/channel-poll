package main.client.model

import java.io.{BufferedReader, InputStreamReader}
import main.client.controller.Controller
import main.shared.data._
import main.shared.communication.MessageReceiver
import main.shared.enums.JsonType
import org.json.{JSONArray, JSONObject}
import org.slf4j.{LoggerFactory}

final object ClientMessageReceiver extends MessageReceiver{
  private val in = new BufferedReader(new InputStreamReader(ClientControl.socket.getInputStream, "UTF-8"))
  private val logger  = LoggerFactory.getLogger(ClientMessageReceiver.getClass)

  def readMessage = {
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
    case JsonType.LOGIN => ClientControl.users += login(jSONObject)
    case JsonType.DISCONNECT => ClientControl.users -= logout(jSONObject)
    case JsonType.STATEMENT => ClientControl.statements += statement(jSONObject)
    case JsonType.POLL => ClientControl += poll(jSONObject)
    case JsonType.POLLANSWER => ClientControl += pollAnswer(jSONObject)
    case JsonType.COMMENT => ClientControl += comment(jSONObject)
    case _ => invalid(jSONObject)
  }

  private def handleLoginSuccessful(json: JSONObject): Unit = {
    val userNames = json.optJSONArray("users")
    for (i <- 0 until userNames.length()) {
      val userID =  userNames.getJSONObject(i).optLong("userid")
      val userName = userNames.getJSONObject(i).optString("username")
      ClientControl.users += TwitterUser(userID, userName)
    }
    val reloadedStatements = json.optJSONArray("statements")
    for (j <- 0 until reloadedStatements.length()) {
      ClientControl.statements += statement(reloadedStatements.getJSONObject(j))
    }
    val reloadedComments = json.optJSONArray("comments")
    for (j <- 0 until reloadedComments.length()) {
      ClientControl += comment(reloadedComments.getJSONObject(j))
    }

    val reloadedPolls = json.optJSONArray("polls")
    for (j <- 0 until reloadedPolls.length()) {
      ClientControl += poll(reloadedPolls.getJSONObject(j))
    }
    val reloadedPollAnswers = json.optJSONArray("pollanswers")
    for (j <- 0 until reloadedPollAnswers.length()) {
      ClientControl += pollAnswer(reloadedPollAnswers.getJSONObject(j))
    }
    Controller.exitLoginView
  }

  private def handleLoginFailed(json: JSONObject): Unit = {
    //TODO show the reason why login failed(e.G. username already taken)
  }
}