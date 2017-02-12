package main.server.communication

import java.io.OutputStreamWriter

import main.server.JsonFiles.SaveJsons
import main.shared.communication.MessageSender
import main.shared.data.{Statement, TwitterUser}
import org.json._
import org.slf4j.{Logger, LoggerFactory}

final class ServerMessageSender(out: OutputStreamWriter) extends MessageSender{
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def writeLoginSuccess(users: Iterable[TwitterUser]): Unit = {
    writeMessage(ServerMessageBuilder.loginSucceded(users))
  }

  def writeStatement(statement: Statement): Unit = {
    val state:JSONObject= ServerMessageBuilder.writeStatement(statement)
    SaveJsons.statementsJsonQueue += state
    writeMessage(state)
  }

  def writeLoginFailed(user: TwitterUser): Unit = {
    writeMessage(ServerMessageBuilder.loginFailed(user))
  }

  def writeMessage(json: JSONObject): Unit = {
    logger.info(json.toString + "\n")
    out.write(json.toString + "\n")
    out.flush()
  }
}
