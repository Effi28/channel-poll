package main.server.communication

import java.io.BufferedReader

import main.shared.enums.JsonType
import main.shared.enums.JsonType.JsonType
import main.shared.data.{Comment, Poll, PollAnswer, Statement}
import org.json.{JSONArray, JSONObject}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.HashMap

class ServerMessageReceiver(in:BufferedReader, client:ClientHandler) {
  private val logger:Logger = LoggerFactory.getLogger(this.getClass)

  def readMessage(): Unit ={
    var jsonText:String = null
    while (true) {
      jsonText = in.readLine()
      if (jsonText != null) {
        val jsonObject:JSONObject = new JSONObject(jsonText)
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
    case JsonType.COMMENT => handleComment(jSONObject)
    case JsonType.POLL => handlePoll(jSONObject)
    case JsonType.SUBSCRIBE => handleSubscribe(jSONObject)
    case JsonType.UNSUBSCRIBE => handleUnsubscribe(jSONObject)
    case _ => handleInvalid(jSONObject)
  }

  private def handleSubscribe(json: JSONObject):Unit={
    client.handleSubscribe(json.optLong("statementID"), json.optString("name"))
  }

  private def handleUnsubscribe(json: JSONObject):Unit={
    client.handleUnsubscribe(json.optLong("statementID"), json.optString("name"))
  }

  private def handleStatement(json: JSONObject): Unit = {
    val message: String = json.optString("message")
    val userID: String = json.optString("userid")
    val userName: String = json.optString("name")
    val screenName: String = json.optString("screenname")
    val pictureURL: String = json.optString("pictureurl")
    val creationDate: String = json.optString("created_at")
    val id: Int = json.optInt("id")
    client.handleStatement(new Statement(message, userID, userName, screenName, pictureURL, creationDate, id))
  }

  private def handlePoll(json: JSONObject): Unit = {
    val statementID: Long = json.optLong("statementid")
    val stamp: String = json.optString("stamp")
    val userID: Long = json.optLong("userid")
    val userName: String = json.optString("username")
    val question: String = json.optString("question")
    val options_Array: JSONArray = json.optJSONArray("options")
    val options: HashMap[Int, (String, Int)] = new HashMap[Int, (String, Int)]

    for(i <- 0 until options_Array.length()){
      val option: JSONObject = options_Array.getJSONObject(i)
      val key:Int = option.optInt("key")
      val optionStr:String = option.optString("optionsstr")
      val likes:Int = option.optInt("likes")
      options += key -> (optionStr,likes)
    }
    val pollID: Int = json.optInt("pollid")
    val thisPoll = new Poll(pollID, statementID, stamp, userID, userName, question, options)
    client.handlePoll(thisPoll)
  }

  private def handlePollAnswer(json: JSONObject): Unit = {
    val userID: Long = json.optLong("userid")
    val userName : String = json.optString("username")
    val question: String = json.optString("question")
    val selectedOptionKey = json.optInt("selectedoptionkey")
    val selectedOptionStr = json.optString("selectedoptionstr")
    val pollID: Int = json.optInt("pollid")
    val timestamp = json.optString("timestamp")
    val statementID: Long = json.optLong("statementid")
    val pollAnswer = new PollAnswer(userID, userName, question, (selectedOptionKey, selectedOptionStr), pollID, timestamp, statementID)
    client.handlePollAnswer(pollAnswer)
  }

  private def handleComment(json: JSONObject): Unit = {
    client.handleComment(new Comment(json.optLong("statementID"), json.optString("message"),json.optString("screenname"), json.optInt("id"), json.optString("stamp")))
  }

  private def handleLogin(json: JSONObject): Unit ={
    client.checkLogin(json.optString("name"))
  }

  private def handleLogout(json: JSONObject): Unit ={
    client.handleLogout(json.optString("name"))
  }

  private def handleInvalid(json: JSONObject): Unit ={
    //TODO
  }
}
