package main.client.model.clientCommunication

import java.io.{BufferedReader, InputStreamReader}
import java.net.Socket
import client.model.clientCommunication.ClientMessageReceiver


/**
  * Created by Effi2 on 17.01.2017.
  */
class ServerHandler (socket:Socket) extends Thread{
  val rec: ClientMessageReceiver = new ClientMessageReceiver(new BufferedReader(new InputStreamReader(socket.getInputStream, "UTF-8")))
  def message = (Thread.currentThread.getName() + "\n").getBytes

  override def run(): Unit = {
    while (true) {
      print("NEW CLIENT")
      rec.readMessage()
    }
  }
}
