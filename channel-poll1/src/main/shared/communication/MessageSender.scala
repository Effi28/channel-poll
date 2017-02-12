package main.shared.communication

import main.client.model.ClientControl
import main.shared.data.{Comment, Poll, PollAnswer, TwitterUser}
import org.json.JSONObject

abstract class MessageSender {
  val MessageBuilder = new MessageBuilder

  def writeLoginMessage(user:TwitterUser): Unit = {
    writeMessage(MessageBuilder.writeLogin(user))
  }

  def writeStComment(comment: Comment): Unit = {
    writeMessage(MessageBuilder.writeComment(comment))
  }

  def writePoll(poll: Poll): Unit = {
    writeMessage(MessageBuilder.writePoll(poll))
  }

  def writePollAnswer(pollAnswer: PollAnswer):Unit={
    writeMessage(MessageBuilder.writePollAnswer(pollAnswer))
  }

  def writeLogout(user:TwitterUser): Unit = {
    writeMessage(MessageBuilder.writeLogout(user))
    ClientControl.close()
  }

  def writeMessage(json:JSONObject):Unit
}
