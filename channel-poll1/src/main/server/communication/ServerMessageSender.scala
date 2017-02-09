package main.server.communication

import java.io.OutputStreamWriter

import main.shared.data.{Comment, Poll, PollAnswer, Statement}
import org.json._
import org.slf4j.{Logger, LoggerFactory}

final class ServerMessageSender(out:OutputStreamWriter) {
  private val logger:Logger = LoggerFactory.getLogger(this.getClass)

  def writeLoginSuccess(users:Iterable[String]):Unit ={
    writeMessage(ServerMessageBuilder.loginSucceded(users))
  }

  def writeStatement(statement: Statement):Unit = {
    writeMessage(ServerMessageBuilder.writeStatement(statement))
  }

  def writeComment(comment: Comment): Unit = {
    writeMessage(ServerMessageBuilder.writeComment(comment))
  }

  def writePoll(poll: Poll): Unit = {
    writeMessage(ServerMessageBuilder.writePoll(poll))
  }

  def writePollAnswer(pollAnswer: PollAnswer): Unit = {
    writeMessage(ServerMessageBuilder.writePollAnswer(pollAnswer))
  }

  def writeNewLogin(nick:String):Unit ={
    writeMessage(ServerMessageBuilder.newLogin(nick))
  }
  def writeLoginFailed(nick:String):Unit ={
    writeMessage(ServerMessageBuilder.loginFailed(nick))
  }

  def writeDisconnect(nick:String):Unit={
    writeMessage(ServerMessageBuilder.userDisconnect(nick))
  }

  private def writeMessage(json:JSONObject):Unit ={
    logger.info(json.toString + "\n")
    out.write(json.toString + "\n")
    out.flush()
  }
}
