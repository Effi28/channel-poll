package client.model.clientCommunication

import java.io.OutputStreamWriter

import main.shared.Message
import org.json._
/**
  * Created by Effi2 on 16.01.2017.
  */
class ClientMessageSender(out:OutputStreamWriter, nick:String) {

  def writeLoginMessage(): Unit ={
    writeMessage(ClientMessageBuilder.loginMessage(nick))
  }

  def writeChatMessage(msg:Message): Unit={
    writeMessage(ClientMessageBuilder.chatMessage(msg))
  }

  def writeMessage(json:JSONObject): Unit ={
    println("CLIENT SENT: " + json.toString + "\n")
    out.write(json.toString + "\n")
    out.flush()
  }
}
