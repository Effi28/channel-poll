package client.model.clientCommunication

import java.io.OutputStreamWriter
import org.json._
/**
  * Created by Effi2 on 16.01.2017.
  */
class ClientMessageSender(out:OutputStreamWriter, nick:String) {

  def writeLoginMessage(): Unit ={
    writeMessage(ClientMessageBuilder.loginMessage(nick))
  }

  def writeChatMessage(msg:String): Unit={
    writeMessage(ClientMessageBuilder.writeChatMessage(nick, msg))
  }

  def writeChatMessage(msg:String, group:String): Unit={
    writeMessage(ClientMessageBuilder.writeChatMessage(nick, msg, group))
  }

  def writeMessage(json:JSONObject): Unit ={
    println("CLIENT SENT: " + json.toString + "\n")
    out.write(json.toString + "\n")
    out.flush()
  }
}
