package client.model.clientCommunication

import java.io.OutputStreamWriter
import main.shared.{Message, Statement, ChatMessage}
import org.json._

class ClientMessageSender(out: OutputStreamWriter, nick: String) {

  def writeLoginMessage(): Unit = {
    writeMessage(ClientMessageBuilder.loginMessage(nick))
  }

  def writeChatMessage(msg: Message): Unit = {
    writeMessage(ClientMessageBuilder.chatMessage(msg))
  }

  /*
  def writeStComment(cmd: ChatMessage): Unit = {
    writeComment(ClientMessageBuilder.comment(cmd))
  }
  */

  def writeMessage(json: JSONObject): Unit = {
    println("CLIENT SENT: " + json.toString + "\n")
    out.write(json.toString + "\n")
    out.flush()
  }

  def writeComment(json: JSONObject): Unit = {
    out.write(json.toString + "\n")
    out.flush()
  }
}
