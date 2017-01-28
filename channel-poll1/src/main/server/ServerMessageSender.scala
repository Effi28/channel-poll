package main.server

import java.io.OutputStreamWriter
import org.json._

class ServerMessageSender(out:OutputStreamWriter) {

  def writeLoginSuccess(users:Iterable[String]): Unit ={
    writeMessage(ServerMessageBuilder.loginSucceded(users))
  }

  def writeNewLogin(nick:String): Unit ={
    writeMessage(ServerMessageBuilder.newLogin(nick))
  }
  def writeLoginFailed(nick:String): Unit ={
    writeMessage(ServerMessageBuilder.loginFailed(nick))
  }

  def writeChatMessage(sender:String, msg:String, stamp:String): Unit={
    writeMessage(ServerMessageBuilder.writeChatMessage(sender, msg, stamp))
  }

  def writeChatMessage(sender:String, msg:String, group:String, stamp:String): Unit={
    writeMessage(ServerMessageBuilder.writeGroupMessage(sender, msg, stamp, group))
  }

  def writeMessage(json:JSONObject): Unit ={
    println("SERVER SENT: " + json.toString + "\n")
    out.write(json.toString + "\n")
    out.flush()
  }
}
