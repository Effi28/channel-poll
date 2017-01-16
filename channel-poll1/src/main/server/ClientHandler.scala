package main.server

import java.io.InputStreamReader
import java.io.BufferedReader
import java.net.Socket

/**
  * Created by Effi2 on 16.01.2017.
  */
class ClientHandler(socket: Socket) extends Thread{

  override def run: Unit = {
  val rec:ServerMessageReceiver = new ServerMessageReceiver()
    val in:BufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"))
    while (true) {
      rec.readMessage(in)
    }
  }
}
