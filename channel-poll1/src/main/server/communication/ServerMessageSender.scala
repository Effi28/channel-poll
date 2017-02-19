package main.server.communication

import java.io.OutputStreamWriter

import main.server.JsonFiles.SaveJsons
import main.shared.communication.MessageSender
import main.shared.data.{Statement, TwitterUser}
import org.json._
import org.slf4j.LoggerFactory
import main.shared.data.Serializable

final class ServerMessageSender(out: OutputStreamWriter) extends MessageSender{
  private val logger = LoggerFactory.getLogger(this.getClass)
  def writeLoginSuccess(users: Iterable[TwitterUser]) = writeMessage(ServerMessageBuilder.loginSucceded(users))
  def writeLoginFailed(user: TwitterUser) = writeMessage(ServerMessageBuilder.loginFailed(user))

  def writeMessage(json: JSONObject) = {
    logger.info(json.toString + "\n")
    out.write(json.toString + "\n")
    out.flush
  }
}
