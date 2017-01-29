package main.client.model.clientCommunication

import java.io.{BufferedReader, InputStreamReader}
import java.net.Socket

import client.model.clientCommunication.ClientMessageReceiver
import main.server.serverCommunication.ClientControl

class ServerHandler (socket:Socket, client:ClientControl) extends Thread{
  val rec: ClientMessageReceiver = new ClientMessageReceiver(new BufferedReader(new InputStreamReader(socket.getInputStream, "UTF-8")), this)

  def message = (Thread.currentThread.getName() + "\n").getBytes

  override def run(): Unit = {
    while (true) {
      rec.readMessage()
    }
  }

  def handleLogin(nick:String): Unit = {
    client.users += nick
  }

  def handleLogout(nick:String): Unit = {
    client.users -= nick
  }
}
