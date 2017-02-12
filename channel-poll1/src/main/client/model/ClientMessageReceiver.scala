package main.client.model

import java.io.{BufferedReader, InputStreamReader}

import main.client.controller.Controller
import main.shared.data.{Comment, Poll, PollAnswer, Statement}
import main.shared.communication.MessageReceiver

import main.shared.enums.JsonType
import org.json.{JSONArray, JSONObject}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.HashMap


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
    case JsonType.LOGIN => handleLogin(jSONObject)
    case JsonType.DISCONNECT => handleLogout(jSONObject)
    case JsonType.STATEMENT => handleStatement(jSONObject)
    case JsonType.POLL => handlePoll(jSONObject)
    case JsonType.POLLANSWER => handlePollAnswer(jSONObject)
    case JsonType.COMMENT => handleComment(jSONObject)
    case _ => handleInvalid(jSONObject)
  }


  private def handleLoginSuccessful(json: JSONObject): Unit = {
    val userNames: JSONArray = json.optJSONArray("users")
    for (i <- 0 until userNames.length()) {
      ServerHandler.handleLogin(userNames.getString(i))
    }
    val reloadedStatements: JSONArray = json.optJSONArray("statements")

    for (j <- 0 until reloadedStatements.length()) {
      handleStatement(reloadedStatements.getJSONObject(j))
    }
    Controller.exitLoginView()
  }

  private def handleStatement(json: JSONObject): Unit = {
    val id: Long = json.optLong("id")
    val userID: Long = json.optLong("userid")
    val userName: String = json.optString("username")
    val pictureURL: String = json.optString("pictureurl")
    val message: String = json.optString("message")
    val timestamp: String = json.optString("timestamp")
    ServerHandler.handleStatement(new Statement(id, userID, userName, pictureURL, message, timestamp))
  }


  private def handleComment(json: JSONObject): Unit = {
    val id: Long = json.optLong("id")
    val statementID: Long = json.optLong("statementid")
    val userID: Long = json.optLong("userid")
    val userName: String = json.optString("username")
    val message: String = json.optString("message")
    val timestamp: String = json.optString("timestamp")

    ServerHandler.handleComment(new Comment(id, statementID, userID, userName, message, timestamp))
  }

  private def handlePollAnswer(json: JSONObject): Unit = {
    val pollID: Long = json.optLong("pollid")
    val statementID: Long = json.optLong("statementid")
    val userID: Long = json.optLong("userid")
    val userName: String = json.optString("username")
    val question: String = json.optString("question")
    val selectedOptionKey: Int = json.optInt("selectedoptionkey")
    val selectedOptionString: String = json.optString("selectedoptionstr")
    val selectedOption: (Int, String) = (selectedOptionKey, selectedOptionString)
    val timestamp: String = json.optString("timestamp")

    ServerHandler.handlePollAnswer(new PollAnswer(pollID, statementID, userID, userName, question, selectedOption, timestamp))
  }

  override def handlePoll(json: JSONObject): Poll = {
    val poll:Poll =  super.handlePoll(json)
    ServerHandler.handlePoll(poll)
    super.handlePoll(json)
  }

  private def handleLoginFailed(json: JSONObject): Unit = {
    //TODO show the reason why login failed(e.G. username already taken)
  }

  private def handleLogin(json: JSONObject): Unit = {
    ServerHandler.handleLogin(json.optString("username"))
  }

  private def handleLogout(json: JSONObject): Unit = {
    ServerHandler.handleLogout(json.optString("username"))
  }

  private def handleInvalid(jSONObject: JSONObject): Unit = {
    logger.error(jSONObject.toString())

  }
}
