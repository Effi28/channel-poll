package main.client.model.clientCommunication

import java.io.{BufferedReader, InputStreamReader}
import java.net.Socket

import client.model.clientCommunication.ClientMessageReceiver
import main.server.serverCommunication.ClientControl
import main.shared.{ChatMessage, Message, Statement}

class ServerHandler(socket: Socket) extends Thread {
  val rec: ClientMessageReceiver = new ClientMessageReceiver(new BufferedReader(new InputStreamReader(socket.getInputStream, "UTF-8")), this)

  def message = (Thread.currentThread.getName() + "\n").getBytes

  override def run(): Unit = {
    while (true) {
      rec.readMessage()
    }
  }

  def handleLogin(nick: String): Unit = {
    ClientControl.users += nick
  }

  def handleLogout(nick: String): Unit = {
    ClientControl.users -= nick
  }

  def handleStatement(statement: Statement): Unit = {
    //ClientControl.statements += statement.ID -> statement
    ClientControl.statements += statement
  }

  /*def handleChatMessage(message: Message): Unit = {
    ClientControl.chatMessages += message.statementID -> message
  }*/

  def handleGlobalChat(message: Message): Unit = {
    ClientControl.globalChat += message.statementID -> message
  }

  def handleGroupChat(message: Message): Unit = {
    ClientControl.groupChat += message.statementID -> message
  }
}
