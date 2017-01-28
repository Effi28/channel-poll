package main.server

import java.net.ServerSocket
import java.util.concurrent.{ExecutorService, Executors}

import scala.collection.mutable
import scala.collection.mutable.MutableList

class ServerControl(port: Int, poolSize: Int) extends Runnable {
  val serverSocket = new ServerSocket(port)
  val pool: ExecutorService = Executors.newFixedThreadPool(poolSize)
  val users:MutableList[String] = new mutable.MutableList[String];
  val chatRooms:MutableList[String] = new mutable.MutableList[String];

  def run() {
    try {
      while (true) {
        // This will block until a connection comes in.
        val socket = serverSocket.accept()
        pool.execute(new ClientHandler(socket, this))
      }
    } finally {
      pool.shutdown()
    }
  }

  def checkLogin(nick:String): Unit ={
    if(users.contains(nick)){

    }
  }
}
