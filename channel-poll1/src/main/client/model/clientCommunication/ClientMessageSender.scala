package client.model.clientCommunication


import main.shared.{Comment, Message, Poll, Statement}
import java.io.{BufferedWriter, OutputStreamWriter}

import main.server.serverCommunication.ClientControl
import org.json.JSONObject


object ClientMessageSender{
  val out:BufferedWriter = new BufferedWriter(new OutputStreamWriter(ClientControl.socket.getOutputStream, "UTF-8"))
  def writeLoginMessage(): Unit ={
    writeMessage(ClientMessageBuilder.writeLogin(ClientControl.user.screenname))
  }

  def writeStComment(comment: Comment): Unit = {
    writeMessage(ClientMessageBuilder.writeComment(comment))
  }

  def writeChatMessage(msg: Message): Unit = {
    writeMessage(ClientMessageBuilder.writeMessage(msg))
  }

  def writePoll(poll:Poll): Unit ={
    writeMessage(ClientMessageBuilder.writePoll(poll))
  }

  def writeLogout(): Unit ={
    writeMessage(ClientMessageBuilder.writeLogoutMessage(ClientControl.user.screenname))
    ClientControl.close()
  }

  def writeSubscribe(statement: Statement): Unit ={
    writeMessage(ClientMessageBuilder.writeSubscribe(ClientControl.user.screenname, statement))
  }

  def writeUnsubscribe(statement: Statement): Unit ={
    writeMessage(ClientMessageBuilder.writeUnsubscribe(ClientControl.user.screenname, statement))
  }

  def writeMessage(json:JSONObject): Unit ={
    println("CLIENT SENT: " + json.toString + "\n")
    out.write(json.toString + "\n")
    out.flush()
  }
}
