package server

import java.net.ServerSocket
import java.net.Socket

import main.server.ClientHandler

/**
  * Created by Effi2 on 16.01.2017.
  */
object Server {
  def main(args: Array[String]): Unit = {
    val port: Int = 8008
    val socket: ServerSocket = new ServerSocket(8008)
    getConnections(socket)
  }

  def getConnections(serverSocket: ServerSocket): Unit = {
    while (!Thread.interrupted()) {
      val socket: Socket = serverSocket.accept()
      if (socket != null) {
        val client : ClientHandler = new ClientHandler(socket)
        client.setDaemon(true)
        client.start()
      }
    }
  }
}
