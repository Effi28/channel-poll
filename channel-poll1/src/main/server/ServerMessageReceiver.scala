package main.server

import java.io.BufferedReader
import java.security.Timestamp

import main.shared.{Comment, Message, PollAnswer, Statement, Poll}
import main.shared.enums.JsonType
import main.shared.enums.JsonType.JsonType
import org.json.{JSONArray, JSONObject}

import scala.collection.mutable
import scala.collection.mutable.HashMap

class ServerMessageReceiver(in:BufferedReader, client:ClientHandler) {

  def readMessage(): Unit ={
    var jsonText:String = null
    while (true) {
      if ((jsonText = in.readLine()) != null) {
        val jsonObject:JSONObject = new JSONObject(jsonText)
        println("SERVER RECEIVED: " + jsonObject)
        matchTest(JsonType.withName(jsonObject.optString("type")), jsonObject)
      }
    }
  }

  def matchTest(x: JsonType, jSONObject: JSONObject): Unit = x match {
    case JsonType.LOGIN => handleLogin(jSONObject)
    case JsonType.DISCONNECT => handleLogout(jSONObject)
    case JsonType.CHAT => handleChat(jSONObject)
    case JsonType.INVALIDMESSAGE => handleInvalid(jSONObject)
    case JsonType.STATEMENT => handleStatement(jSONObject)
    case JsonType.COMMENT => handleComment(jSONObject)
    case JsonType.POLL => handlePoll(jSONObject)
    case JsonType.SUBSCRIBE => handleSubscribe(jSONObject)
    case JsonType.UNSUBSCRIBE => handleUnsubscribe(jSONObject)
    case _ => handleInvalid(jSONObject)
  }

  def handleSubscribe(jSONObject: JSONObject):Unit={
    client.handleSubscribe(jSONObject.optString("statementID"), jSONObject.optLong("name"))
  }

  def handleUnsubscribe(jSONObject: JSONObject):Unit={
    client.handleUnsubscribe(jSONObject.optString("statementID"), jSONObject.optLong("name"))
  }

  def handleStatement(jSONObject: JSONObject): Unit = {
    val message: String = jSONObject.optString("message")
    val userID: String = jSONObject.optString("userid")
    val userName: String = jSONObject.optString("name")
    val screenName: String = jSONObject.optString("screenname")
    val pictureURL: String = jSONObject.optString("pictureurl")
    val creationDate: String = jSONObject.optString("created_at")
    val id: Int = jSONObject.optInt("id")
    client.handleStatement(new Statement(message, userID, userName, screenName, pictureURL, creationDate, id))
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
    val thisPoll = new Poll(pollID, statementID, stamp, user, question, options)
    client.handlePoll(thisPoll)
  }

  def handlePollAnswer(jSONObject: JSONObject): Unit = {
    val userID: Long = jSONObject.optLong("userid")
    val question: String = jSONObject.optString("question")
    val selectedOptionKey = jSONObject.optInt("selectedoptionkey")
    val selectedOptionStr = jSONObject.optString("selectedoptionstr")
    val pollID: Int = jSONObject.optInt("pollid")
    val timestamp = jSONObject.optString("timestamp")
    val statementID: Long = jSONObject.optLong("statementid")


    val pollAnswer = new PollAnswer(userID, question, (selectedOptionKey, selectedOptionStr), pollID, timestamp, statementID)
    client.handlePollAnswer(pollAnswer)
  }

  def handleComment(json: JSONObject): Unit = {
    client.handleComment(new Comment(json.optLong("statementID"), json.optString("message"),json.optString("screenname"), json.optInt("id"), json.optString("stamp")))
  }

  def handleLogin(jSONObject: JSONObject): Unit ={
    client.checkLogin(jSONObject.optString("name"))
  }

  def handleLogout(jSONObject: JSONObject): Unit ={
    client.handleLogout(jSONObject.optString("name"))
  }


  def handleChat(jSONObject: JSONObject): Unit ={
    val sender:String =jSONObject.optString("senderID")
    val stamp:String =jSONObject.optString("stamp")
    val msg:String =jSONObject.optString("message")
    val rcv:String = jSONObject.optString("groupID")
    val msgT:Message = new Message(sender, stamp, msg, rcv, Server.chatID+1)
    if(rcv == null){
      Server.broadcastGlobalMessage(msgT)
    }
    else{
      Server.broadcastGroupMessage(msgT)
    }
  }


  def handleInvalid(jSONObject: JSONObject): Unit ={

  }
}
