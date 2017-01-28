package main.server

import java.io.{BufferedReader, InputStreamReader}
import java.net.Socket

class ClientHandler(socket:Socket, server:ServerControl) extends Runnable{
  val rec: ServerMessageReceiver = new ServerMessageReceiver(new BufferedReader(new InputStreamReader(socket.getInputStream, "UTF-8")), this)
  def message = (Thread.currentThread.getName() + "\n").getBytes

  def run(): Unit = {
    while (true) {
      rec.readMessage()
    }
  }

  def checkLogin(nick:String): Unit = {
    server.checkLogin(nick:String)
  }
}
