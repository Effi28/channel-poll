package main.client.model.clientCommunication

import client.model.clientCommunication.ClientMessageReceiver
import main.server.serverCommunication.ClientControl
import main.shared.{Comment, Message, Statement}

object ServerHandler extends Thread{
  def message = (Thread.currentThread.getName() + "\n").getBytes

  override def run(): Unit = {
    while (true) {
      ClientMessageReceiver.readMessage()
    }
  }

  def handleLogin(nick:String): Unit = {
    ClientControl.users += nick
  }

  def handleLogout(nick:String): Unit = {
    ClientControl.users -= nick
  }

  def handleStatement(statement:Statement):Unit = {
    ClientControl.statements += statement
  }

  def handleComment(comment: Comment): Unit = {
    ClientControl.comments.get(comment.statement).get += comment
  }

  def handleGlobalChat(message:Message): Unit ={
    ClientControl.globalChat += message.id -> message
  }

  def handleGroupChat(message:Message): Unit = {
    ClientControl.groupChat += message.id -> message
  }
}
