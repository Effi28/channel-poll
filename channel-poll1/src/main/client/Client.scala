package client

import main.server.serverCommunication.ClientControl

/**
  * Created by Effi2 on 16.01.2017.
  */
object Client {
  def main(args: Array[String]): Unit = {
    val address:String = "localhost"
    val port:Int = 8008
    var clientControl:ClientControl = new ClientControl()
    clientControl.setupClient(address, port)
  }
}
