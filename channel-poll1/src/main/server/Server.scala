package server

import java.io.{BufferedReader, InputStreamReader}
import java.net.{ServerSocket, Socket}
import java.util.concurrent.{ExecutorService, Executors}

/**
  * Created by Effi2 on 16.01.2017.
  */
object Server {
  def main(args: Array[String]): Unit = {
    val port: Int = 8008
    (new NetworkService(port, 2)).run
  }

  class NetworkService(port: Int, poolSize: Int) extends Runnable {
    val serverSocket = new ServerSocket(port)
    val pool: ExecutorService = Executors.newFixedThreadPool(poolSize)

    def run() {
      try {
        while (true) {
          // This will block until a connection comes in.
          val socket = serverSocket.accept()
          pool.execute(new Handler(socket))
        }
      } finally {
        pool.shutdown()
      }
    }
  }

  class Handler(socket: Socket) extends Runnable {
    def message = (Thread.currentThread.getName() + "\n").getBytes

    def run(): Unit = {
      print("NEW CLIENT: " + socket.toString)
      val in: BufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream, "UTF-8"))
      var jsonText:String = null
      while (true) {
        if ((jsonText = in.readLine()) != null) {
          print(jsonText + "\n")
        }
      }
    }
  }
  }
