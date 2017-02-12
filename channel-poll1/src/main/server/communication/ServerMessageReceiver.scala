package main.server.communication

import java.io.BufferedReader

import main.shared.enums.JsonType
import main.shared.enums.JsonType.JsonType
import main.shared.data.{Comment, Poll, PollAnswer, Statement}
import org.json.{JSONObject}
import org.slf4j.{Logger, LoggerFactory}
import main.shared.communication.MessageReceiver

class ServerMessageReceiver(in: BufferedReader, client: ClientHandler) extends MessageReceiver{
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def readMessage(): Unit = {
    var jsonText: String = null
    while (true) {
      jsonText = in.readLine()
      if (jsonText != null) {
        val jsonObject: JSONObject = new JSONObject(jsonText)
        logger.info(jsonObject.toString())
        matchTest(JsonType.withName(jsonObject.optString("type")), jsonObject)
      }
    }
  }

  private def matchTest(x: JsonType, jSONObject: JSONObject): Unit = x match {
    case JsonType.LOGIN => handleLogin(jSONObject)
    case JsonType.DISCONNECT => handleLogout(jSONObject)
    case JsonType.INVALIDMESSAGE => handleInvalid(jSONObject)
    case JsonType.STATEMENT => handleStatement(jSONObject)
    case JsonType.COMMENT =>
      // SaveJsons.commentsJsonQueue += jSONObject
      handleComment(jSONObject)
    case JsonType.POLL =>
      //SaveJsons.pollsQueue += jSONObject
      handlePoll(jSONObject)
    case JsonType.SUBSCRIBE => handleSubscribe(jSONObject)
    case JsonType.UNSUBSCRIBE => handleUnsubscribe(jSONObject)
    case JsonType.POLLANSWER => handlePollAnswer(jSONObject)
    //SaveJsons.pollsAnswersQueue += jSONObject
    case _ => handleInvalid(jSONObject)
  }

  private def handleSubscribe(json: JSONObject): Unit = {
    client.handleSubscribe(json.optLong("statementid"), json.optString("username"))
  }

  private def handleUnsubscribe(json: JSONObject): Unit = {
    client.handleUnsubscribe(json.optLong("statementid"), json.optString("username"))
  }

  private def handleStatement(json: JSONObject): Unit = {
    val id: Long = json.optLong("id")
    val userID: Long = json.optLong("userid")
    val userName: String = json.optString("username")
    val pictureURL: String = json.optString("pictureurl")
    val message: String = json.optString("message")
    val timestamp: String = json.optString("timestamp")
    client.handleStatement(new Statement(id, userID, userName, pictureURL, message, timestamp))
  }

  override def handlePoll(json: JSONObject): Poll = {
    val poll:Poll =  super.handlePoll(json)
    client.handlePoll(poll)
    poll
  }

  private def handlePollAnswer(json: JSONObject): Unit = {
    val pollID: Int = json.optInt("pollid")
    val statementID: Long = json.optLong("statementid")
    val userID: Long = json.optLong("userid")
    val userName: String = json.optString("username")
    val question: String = json.optString("question")
    val selectedOptionKey = json.optInt("selectedoptionkey")
    val selectedOptionStr = json.optString("selectedoptionstr")
    val timestamp = json.optString("timestamp")
    val pollAnswer = new PollAnswer(pollID, statementID, userID, userName, question, (selectedOptionKey, selectedOptionStr), timestamp)
    client.handlePollAnswer(pollAnswer)
  }

  private def handleComment(json: JSONObject): Unit = {
    val id: Long = json.optLong("id")
    val statementID: Long = json.optLong("statementid")
    val userID: Long = json.optLong("userid")
    val userName: String = json.optString("username")
    val message: String = json.optString("message")
    val timestamp: String = json.optString("timestamp")

    client.handleComment(new Comment(id, statementID, userID, userName, message, timestamp))
  }

  private def handleLogin(json: JSONObject): Unit = {
    client.checkLogin(json.optString("username"))
  }

  private def handleLogout(json: JSONObject): Unit = {
    client.handleLogout(json.optString("username"))
  }

  private def handleInvalid(json: JSONObject): Unit = {
    //TODO
  }
}
