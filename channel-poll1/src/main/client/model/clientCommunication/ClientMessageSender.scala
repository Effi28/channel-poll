package client.model.clientCommunication


import main.shared.{Comment, Message}
import java.io.{BufferedWriter, OutputStreamWriter}
import main.server.serverCommunication.ClientControl
import org.json.JSONObject


object ClientMessageSender{
  val out:BufferedWriter = new BufferedWriter(new OutputStreamWriter(ClientControl.socket.getOutputStream, "UTF-8"))
  def writeLoginMessage(): Unit ={
    writeMessage(ClientMessageBuilder.loginMessage(ClientControl.nick))
  }

  def writeChatMessage(msg: Message): Unit = {
    writeMessage(ClientMessageBuilder.chatMessage(msg))
  }



  def writeLogout(): Unit ={
    writeMessage(ClientMessageBuilder.writeLogoutMessage(ClientControl.nick))
    ClientControl.close()
  }

  def writeMessage(json:JSONObject): Unit ={
    println("CLIENT SENT: " + json.toString + "\n")
    out.write(json.toString + "\n")
    out.flush()
  }
}
