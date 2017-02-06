package client.model.clientCommunication

import java.io.{BufferedReader, InputStreamReader}

import main.client.controller.Controller
import main.client.model.clientCommunication.ServerHandler
import main.server.serverCommunication.ClientControl
import main.shared.{Comment, Message, Poll, Statement}
import main.shared.enums.JsonType
import org.json.{JSONArray, JSONObject}

import scala.collection.mutable.HashMap


object ClientMessageReceiver {
  val in:BufferedReader = new BufferedReader(new InputStreamReader(ClientControl.socket.getInputStream, "UTF-8"))

  def readMessage(): Unit ={
    var jsonText:String = null
    while (!ClientControl.socket.isClosed) {
      if ((jsonText = in.readLine()) != null) {
        val jsonObject: JSONObject = new JSONObject(jsonText)
        println("CLIENT RECEIVED: " + jsonObject)
        matchTest(JsonType.withName(jsonObject.optString("type")), jsonObject)
      }
    }
  }

  def matchTest(x: JsonType.JsonType, jSONObject: JSONObject): Unit = x match {
    case JsonType.LOGINSUCCESS => handleLoginSuccessful(jSONObject)
    case JsonType.LOGINFAILED => handleLoginFailed(jSONObject)
    case JsonType.LOGIN => handleLogin(jSONObject)
    case JsonType.DISCONNECT => handleLogout(jSONObject)
    case JsonType.CHAT => handleChat(jSONObject)
    case JsonType.INVALIDMESSAGE => handleInvalid(jSONObject)
    case JsonType.STATEMENT => handleStatement(jSONObject)
    case JsonType.POLL => handlePoll(jSONObject)
    case JsonType.POLLANSWER => handlePollAnswer(jSONObject)
    case JsonType.COMMENT => handleComment(jSONObject)
    case _ => handleInvalid(jSONObject)
  }


  def handleLoginSuccessful(json:JSONObject): Unit ={
    val userNames: JSONArray = json.optJSONArray("users")
    for(i <- 0 until userNames.length()){
      ServerHandler.handleLogin(userNames.getString(i))
    }
    Controller.exitLoginView()
  }

  def handleStatement(json: JSONObject): Unit = {
    val message: String = json.optString("message")
    val userID: String = json.optString("userid")
    val userName: String = json.optString("name")
    val screenName: String = json.optString("screenname")
    val pictureURL: String = json.optString("pictureurl")
    val creationDate: String = json.optString("created_at")
    val id: Int = json.optInt("id")
    ServerHandler.handleStatement(new Statement(message, userID, userName, screenName, pictureURL, creationDate, id))
  }


  def handleComment(json: JSONObject): Unit = {
    ServerHandler.handleComment(new Comment(json.optLong("statementID"), json.optString("message"),json.optString("screenname"), json.optInt("id"), json.optString("stamp")))
  }

  def handlePollAnswer(jSONObject: JSONObject): Unit = {
    //ServerHandler.handlePollAnswer()  todo
  }

  def handlePoll(jSONObject: JSONObject): Unit = {
    val statementID: Long = jSONObject.optLong("statementid")
    val stamp: String = jSONObject.optString("stamp")
    val user: String = jSONObject.optString("user")
    val question: String = jSONObject.optString("question")
    val options_Array: JSONArray = jSONObject.optJSONArray("options")
    val options: HashMap[Int, (String, Int)] = new HashMap[Int, (String, Int)]

    for(i <- 0 until options_Array.length()){
      val option: JSONObject = options_Array.getJSONObject(i)
      val key:Int = option.optInt("key")
      val optionStr:String = option.optString("optionsstr")
      val likes:Int = option.optInt("likes")
      options += key -> (optionStr,likes)
    }
    val pollID: Int = jSONObject.optInt("pollid")
    val thisPoll = new Poll(pollID, statementID, user, stamp, question, options)
    ServerHandler.handlePoll(thisPoll)
  }


    def handleLoginFailed(jSONObject:JSONObject): Unit ={
    //TODO show the reason why login failed(e.G. username already taken)
  }

  def handleLogin(jSONObject:JSONObject): Unit ={
    ServerHandler.handleLogin(jSONObject.optString("name"))
  }

  def handleLogout(jSONObject:JSONObject): Unit ={
    ServerHandler.handleLogout(jSONObject.optString("name"))
  }

  def handleChat(jSONObject:JSONObject): Unit = {
    val msg:Message = new Message(jSONObject.optString("senderID"), jSONObject.optString("stamp"), jSONObject.optString("message"), jSONObject.optString("groupID"), jSONObject.optInt("id"))
    if(msg.rcv == null){
      ServerHandler.handleGlobalChat(msg)
    }
    else{
      ServerHandler.handleGroupChat(msg)
    }
  }

  def handleInvalid(jSONObject: JSONObject):Unit = {
  }
}
