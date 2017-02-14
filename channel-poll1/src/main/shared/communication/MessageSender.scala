package main.shared.communication

import main.client.model.ClientControl
import main.server.JsonFiles.SaveJsons
import main.shared.data.{Comment, Poll, PollAnswer, TwitterUser}
import org.json.JSONObject

abstract class MessageSender {
  val MessageBuilder = new MessageBuilder

  def writeLoginMessage(user: TwitterUser): Unit = {
    writeMessage(MessageBuilder.writeLogin(user))
  }

  def writeStComment(comment: Comment): Unit = {
    val commentJson = MessageBuilder.writeComment(comment)
    SaveJsons.commentsJsonQueue += commentJson
    writeMessage(commentJson)
  }

  def writePoll(poll: Poll): Unit = {
    val pollJson = MessageBuilder.writePoll(poll)
    SaveJsons.pollsQueue += pollJson
    writeMessage(pollJson)
  }

  def writePollAnswer(pollAnswer: PollAnswer): Unit = {
    val pollAnswerJson = MessageBuilder.writePollAnswer(pollAnswer)
    SaveJsons.pollsAnswersQueue += pollAnswerJson
    writeMessage(pollAnswerJson)
  }

  def writeLogout(user: TwitterUser): Unit = {
    writeMessage(MessageBuilder.writeLogout(user))
    ClientControl.close()
  }

  def writeMessage(json: JSONObject): Unit
}
