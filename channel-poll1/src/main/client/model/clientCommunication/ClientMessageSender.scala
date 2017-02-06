package client.model.clientCommunication


import main.shared.{Comment, Message, Poll}
import java.io.{BufferedWriter, OutputStreamWriter}

import main.server.serverCommunication.ClientControl
import org.json.JSONObject


object ClientMessageSender{
  val out:BufferedWriter = new BufferedWriter(new OutputStreamWriter(ClientControl.socket.getOutputStream, "UTF-8"))
  def writeLoginMessage(): Unit ={
    writeMessage(ClientMessageBuilder.loginMessage(ClientControl.user.screenname))
  }

  def writeStComment(comment: Comment): Unit = {
    writeMessage(ClientMessageBuilder.comment(comment))
  }


  def writeChatMessage(msg: Message): Unit = {
    writeMessage(ClientMessageBuilder.chatMessage(msg))
  }

  def writePoll(poll:Poll): Unit ={
    writeMessage(ClientMessageBuilder.writePoll(poll))
  }

  def writeLogout(): Unit ={
    writeMessage(ClientMessageBuilder.writeLogoutMessage(ClientControl.user.screenname))
    ClientControl.close()
  }

  def writeMessage(json:JSONObject): Unit ={
    println("CLIENT SENT: " + json.toString + "\n")
    out.write(json.toString + "\n")
    out.flush()
  }
}
