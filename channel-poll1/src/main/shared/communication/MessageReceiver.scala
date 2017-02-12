package main.shared.communication

import main.shared.data.Poll
import org.json.{JSONArray, JSONObject}

import scala.collection.mutable.{ArrayBuffer, HashMap}
import main.shared.data.Option

abstract class MessageReceiver {

  def handlePoll(json: JSONObject): Poll = {
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
}
