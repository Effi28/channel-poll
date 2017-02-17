package main.shared.communication

import main.shared.data.{Comment, Option, Poll, PollAnswer, Statement, TwitterUser}
import org.json.{JSONArray, JSONObject}

import scala.collection.mutable.ArrayBuffer

abstract class MessageReceiver {

  def readMessage
  def login(json: JSONObject) = TwitterUser(json.optLong("userid"), json.optString("username"))
  def logout(json: JSONObject) = TwitterUser(json.optLong("userid"), json.optString("username"))
  def invalid(json: JSONObject) {}//TODO

  def poll(json: JSONObject): Poll = {
    val pollID = json.optLong("id")
    val statementID = json.optLong("statementid")
    val userID = json.optLong("userid")
    val userName = json.optString("username")
    val question = json.optString("question")
    val options_Array = json.optJSONArray("options")
    val options = new ArrayBuffer[Option]
    for (i <- 0 until options_Array.length()) {
      val option = options_Array.getJSONObject(i)
      val key = option.optInt("key")
      val optionStr = option.optString("optionsstr")
      val likes = option.optInt("likes")
      options += new Option(key, optionStr, likes)
    }
    val timestamp = json.optString("timestamp")
    new Poll(pollID, statementID, userID, userName, question, options, timestamp)
  }

  def statement(json: JSONObject): Statement = {
    val id = json.optLong("id")
    val userID = json.optLong("userid")
    val userName = json.optString("username")
    val pictureURL = json.optString("pictureurl")
    val message = json.optString("message")
    val timestamp = json.optString("timestamp")
    new Statement(id, userID, userName, pictureURL, message, timestamp)
  }

  def pollAnswer(json: JSONObject): PollAnswer = {
    val pollID = json.optLong("pollid")
    val statementID = json.optLong("statementid")
    val userID = json.optLong("userid")
    val userName = json.optString("username")
    val question = json.optString("question")
    val selectedOptionKey = json.optInt("selectedoptionkey")
    val selectedOptionStr = json.optString("selectedoptionstr")
    val timestamp = json.optString("timestamp")
    new PollAnswer(pollID, statementID, userID, userName, question, (selectedOptionKey, selectedOptionStr), timestamp)
  }

  def comment(json: JSONObject): Comment = {
    val id = json.optLong("id")
    val statementID = json.optLong("statementid")
    val userID = json.optLong("userid")
    val userName = json.optString("username")
    val message = json.optString("message")
    val timestamp = json.optString("timestamp")
    new Comment(id, statementID, userID, userName, message, timestamp)
  }
}
