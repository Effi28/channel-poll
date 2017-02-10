package main.client.model

import java.io.{BufferedReader, InputStreamReader}

import main.client.controller.Controller
import main.shared.data.{Comment, Poll, Statement}
import main.shared.enums.JsonType
import main.shared.data.{Poll, Statement}
import org.json.{JSONArray, JSONObject}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable.HashMap


final object ClientMessageReceiver {
  private val in:BufferedReader = new BufferedReader(new InputStreamReader(ClientControl.socket.getInputStream, "UTF-8"))
  private val logger:Logger = LoggerFactory.getLogger(ClientMessageReceiver.getClass)

  def readMessage(): Unit ={
    var jsonText:String = null
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


  private def handleLoginSuccessful(json:JSONObject): Unit ={
    val userNames: JSONArray = json.optJSONArray("users")
    for(i <- 0 until userNames.length()){
      ServerHandler.handleLogin(userNames.getString(i))
    }
    Controller.exitLoginView()
  }

  private def handleStatement(json: JSONObject): Unit = {
    val message: String = json.optString("message")
    val userID: Long = json.optLong("userid")
    val userName: String = json.optString("username")
    val pictureURL: String = json.optString("pictureurl")
    val timestamp: String = json.optString("timestamp")
    val id: Int = json.optInt("id")
    ServerHandler.handleStatement(new Statement(id, userID, userName, pictureURL,message, timestamp))
  }


  private def handleComment(json: JSONObject): Unit = {
    ServerHandler.handleComment(new Comment(json.optLong("id"), json.optLong("statementid"),json.optLong("userid"),json.optString("username"), json.optString("message"), json.optString("timestamp")))
  }

  private def handlePollAnswer(json: JSONObject): Unit = {
    //ServerHandler.handlePollAnswer()  todo
  }

  private def handlePoll(json: JSONObject): Unit = {
    val pollID: Long = json.optInt("id")
    val statementID: Long = json.optLong("statementid")
    val userID: Long = json.optLong("userid")
    val userName : String = json.optString("username")
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
    val timestamp: String = json.optString("timestamp")
    val thisPoll = new Poll(pollID, statementID, userID, userName, question, options, timestamp)
    ServerHandler.handlePoll(thisPoll)
  }

  private def handleLoginFailed(json:JSONObject): Unit ={
    //TODO show the reason why login failed(e.G. username already taken)
  }

  private def handleLogin(json:JSONObject): Unit ={
    ServerHandler.handleLogin(json.optString("username"))
  }

  private def handleLogout(json:JSONObject): Unit ={
    ServerHandler.handleLogout(json.optString("username"))
  }

  private def handleInvalid(jSONObject: JSONObject):Unit = {
    logger.error(jSONObject.toString())

  }
}
