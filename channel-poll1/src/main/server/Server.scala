package server

import java.net.{ServerSocket, Socket}
import java.util.concurrent.{ExecutorService, Executors}

import main.server.{ClientHandler, ServerControl}

import scala.collection.mutable
import scala.collection.mutable.MutableList

/**
  * Created by Effi2 on 16.01.2017.
  */
object Server {
  def main(args: Array[String]): Unit = {
    val port: Int = 8008
    (new ServerControl(port, 2)).run
  }
}
