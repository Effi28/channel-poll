package main.shared.communication

import main.shared.data._
import main.shared.enums.JsonType
import main.shared.data.Serializable
import org.json.{JSONArray, JSONObject}

class MessageBuilder {

  def writeLogin(user: TwitterUser): JSONObject = {
    val Jmsg = new JSONObject
    Jmsg.put("type", JsonType.LOGIN)
    Jmsg.put("username", user.userName)
    Jmsg.put("userid", user.ID)
  }

  def writeLogout(user: TwitterUser): JSONObject = {
    val Jmsg = new JSONObject
    Jmsg.put("type", JsonType.DISCONNECT)
    Jmsg.put("username", user.userName)
    Jmsg.put("userid", user.ID)
  }

   def writeData(serializable: Serializable): JSONObject = serializable match {
    case poll:Poll =>  writePoll(poll)
    case pollAnswer:PollAnswer =>  writePollAnswer(pollAnswer)
    case comment:Comment => writeComment(comment)
    case statement:Statement => writeStatement(statement)
  }

  private def writeComment(comment: Comment): JSONObject = {
    val Jmsg = new JSONObject
    Jmsg.put("type", JsonType.COMMENT)
    Jmsg.put("id", comment.ID)
    Jmsg.put("statementid", comment.statementID)
    Jmsg.put("userid", comment.userID)
    Jmsg.put("username", comment.userName)
    Jmsg.put("message", comment.message)
    Jmsg.put("timestamp", comment.timestamp)
  }

  private def writePoll(poll: Poll): JSONObject = {
    val Jmsg = new JSONObject
    Jmsg.put("type", JsonType.POLL)
    Jmsg.put("id", poll.ID)
    Jmsg.put("statementid", poll.statementID)
    Jmsg.put("userid", poll.userID)
    Jmsg.put("username", poll.userName)
    Jmsg.put("question", poll.question)
    val arr: JSONArray = new JSONArray
    for (option <- poll.options) {
      val obj: JSONObject = new JSONObject
      obj.put("key", option.key)
      obj.put("optionsstr", option.name)
      obj.put("likes", option.likes)
      arr.put(obj)
    }
    Jmsg.put("options", arr)
    Jmsg.put("timestamp", poll.timestamp)

  }

  private def writePollAnswer(pollAnswer: PollAnswer): JSONObject = {
    val Jmsg = new JSONObject
    Jmsg.put("type", JsonType.POLLANSWER)
    Jmsg.put("pollid", pollAnswer.pollID)
    Jmsg.put("statementid", pollAnswer.statementID)
    Jmsg.put("userid", pollAnswer.userID)
    Jmsg.put("username", pollAnswer.userName)
    Jmsg.put("question", pollAnswer.question)
    Jmsg.put("selectedoptionkey", pollAnswer.selectedOption._1)
    Jmsg.put("selectedoptionstr", pollAnswer.selectedOption._2)
    Jmsg.put("timestamp", pollAnswer.timestamp)
  }

  private def writeStatement(statement: Statement): JSONObject = {
    val Jmsg = new JSONObject
    Jmsg.put("type", JsonType.STATEMENT)
    Jmsg.put("id", statement.ID)
    Jmsg.put("userid", statement.userID)
    Jmsg.put("username", statement.userName)
    Jmsg.put("pictureurl", statement.pictureURL)
    Jmsg.put("message", statement.message)
    Jmsg.put("timestamp", statement.timestamp)
  }
}
