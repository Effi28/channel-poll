package main.server

import java.io.{BufferedReader, InputStreamReader}
import java.net.Socket
/**
  * Created by Effi2 on 17.01.2017.
  */
class ClientHandler(socket:Socket) extends Runnable{
  val rec: ServerMessageReceiver = new ServerMessageReceiver(new BufferedReader(new InputStreamReader(socket.getInputStream, "UTF-8")))
  def message = (Thread.currentThread.getName() + "\n").getBytes

  def run(): Unit = {
    print("NEW CLIENT")
    while (true) {
      rec.readMessage()
    }
  }
}
