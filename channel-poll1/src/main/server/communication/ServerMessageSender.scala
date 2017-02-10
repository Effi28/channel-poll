package main.server.communication

import java.io.OutputStreamWriter

import main.server.JsonFiles.SaveJsons
import main.shared.data.{Comment, Poll, PollAnswer, Statement}
import org.json._
import org.slf4j.{Logger, LoggerFactory}

final class ServerMessageSender(out:OutputStreamWriter) {
  private val logger:Logger = LoggerFactory.getLogger(this.getClass)

  def writeLoginSuccess(users:Iterable[String]):Unit ={
    writeMessage(ServerMessageBuilder.loginSucceded(users))
  }

  def writeStatement(statement: Statement):Unit = {
    val statementJson = ServerMessageBuilder.writeStatement(statement)
    SaveJsons.statementsJsonQueue += statementJson
    writeMessage(statementJson)
  }

  def writeComment(comment: Comment): Unit = {
    val commentJson = ServerMessageBuilder.writeComment(comment)
    SaveJsons.commentsJsonQueue += commentJson
    writeMessage(commentJson)
  }

  def writePoll(poll: Poll): Unit = {
    val pollJson = ServerMessageBuilder.writePoll(poll)
    SaveJsons.pollsQueue += pollJson
    writeMessage(pollJson)
  }

  def writePollAnswer(pollAnswer: PollAnswer): Unit = {
    val pollAnswerJson = ServerMessageBuilder.writePollAnswer(pollAnswer)
    SaveJsons.pollsAnswersQueue += pollAnswerJson
    writeMessage(pollAnswerJson)
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
