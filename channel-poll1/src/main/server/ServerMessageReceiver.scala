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
    case _ => handleInvalid(jSONObject)
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
    /**
    for(option: JSONObject <- options_Array){
      val key: Int = option.optInt("Int")
      val option_str: String = option.optString("key")
      val option_likes: Int = option.optInt("likes")
      val opt_tuple = (option_str, option_likes)
      options += key -> opt_tuple
    }**/
    val pollID: Int = jSONObject.optInt("pollid")

    val thisPoll = new Poll(pollID, statementID, user, stamp, question, options)
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
    val arr:JSONArray = json.optJSONArray("likes")
    //TODO check if array is ok
    val likes:Array[String] = new Array[String](arr.length())
    for(i <- 0 until arr.length()){
      likes.update(i, arr.toString)
    }

    val statement:Statement = getStatement(json.optJSONObject("statement"))
    client.handleComment(new Comment(statement, json.optString("message"),json.optString("screenname"), json.optInt("id")))
  }

  def getStatement(json: JSONObject): Statement = {
    val jsonStatement:JSONObject = json.optJSONObject("statement")
    val message: String = jsonStatement.optString("message")
    val userID: String = jsonStatement.optString("userid")
    val userName: String = jsonStatement.optString("name")
    val screenName: String = jsonStatement.optString("screenname")
    val pictureURL: String = jsonStatement.optString("pictureurl")
    val creationDate: String = jsonStatement.optString("created_at")
    val id: Int = jsonStatement.optInt("id")
    new Statement(message, userID, userName, screenName, pictureURL, creationDate, id)
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
