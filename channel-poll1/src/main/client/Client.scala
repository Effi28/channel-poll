package client

import main.server.serverCommunication.ClientControl

/**
  * Created by Effi2 on 16.01.2017.
  */
object Client {
  def main(args: Array[String]): Unit = {

    //TODO START loginView here
    //TODO START Controller here
    val nick:String = "TODO"
    val address:String = "localhost"
    val port:Int = 8008
    var clientControl:ClientControl = new ClientControl(address, port, nick)
    clientControl.setupClient()
  }
}
