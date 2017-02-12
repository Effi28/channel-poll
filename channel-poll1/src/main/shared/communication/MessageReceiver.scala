package main.shared.communication

import main.shared.data.{Comment, Option, Poll, PollAnswer, Statement, TwitterUser}
import org.json.{JSONArray, JSONObject}

import scala.collection.mutable.ArrayBuffer

abstract class MessageReceiver {

  def readMessage():Unit

  def poll(json: JSONObject): Poll = {
    val pollID: Int = json.optInt("id")
    val statementID: Long = json.optLong("statementid")
    val userID: Long = json.optLong("userid")
    val userName: String = json.optString("username")
    val question: String = json.optString("question")
    val options_Array: JSONArray = json.optJSONArray("options")
    val options: ArrayBuffer[Option] = new ArrayBuffer[Option]
    for (i <- 0 until options_Array.length()) {
      val option: JSONObject = options_Array.getJSONObject(i)
      val key: Int = option.optInt("key")
      val optionStr: String = option.optString("optionsstr")
      val likes: Int = option.optInt("likes")
      options += new Option(key, optionStr, likes)
    }
    val timestamp: String = json.optString("timestamp")
    new Poll(pollID, statementID, userID, userName, question, options, timestamp)
  }

  def statement(json: JSONObject): Statement = {
    val id: Long = json.optLong("id")
    val userID: Long = json.optLong("userid")
    val userName: String = json.optString("username")
    val pictureURL: String = json.optString("pictureurl")
    val message: String = json.optString("message")
    val timestamp: String = json.optString("timestamp")
    new Statement(id, userID, userName, pictureURL, message, timestamp)
  }

  def pollAnswer(json: JSONObject): PollAnswer = {
    val pollID: Int = json.optInt("pollid")
    val statementID: Long = json.optLong("statementid")
    val userID: Long = json.optLong("userid")
    val userName: String = json.optString("username")
    val question: String = json.optString("question")
    val selectedOptionKey = json.optInt("selectedoptionkey")
    val selectedOptionStr = json.optString("selectedoptionstr")
    val timestamp = json.optString("timestamp")
    new PollAnswer(pollID, statementID, userID, userName, question, (selectedOptionKey, selectedOptionStr), timestamp)
  }

  def comment(json: JSONObject): Comment = {
    val id: Long = json.optLong("id")
    val statementID: Long = json.optLong("statementid")
    val userID: Long = json.optLong("userid")
    val userName: String = json.optString("username")
    val message: String = json.optString("message")
    val timestamp: String = json.optString("timestamp")
    new Comment(id, statementID, userID, userName, message, timestamp)
  }

  def login(json: JSONObject): TwitterUser = {
    new TwitterUser(json.optLong("userid"), json.optString("username"))
  }

  def logout(json: JSONObject): TwitterUser = {
    new TwitterUser(json.optLong("userid"), json.optString("username"))
  }

  def invalid(json: JSONObject): Unit = {
    //TODO
  }
}
