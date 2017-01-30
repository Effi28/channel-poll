package main.client

import main.client.controller.Controller
import main.client.view.LoginView
import main.server.serverCommunication.ClientControl

/**
  * Created by Effi2 on 16.01.2017.
  */
object Client {
  def main(args: Array[String]): Unit = {
    val address:String = "localhost"
    val port:Int = 8008

    //TODO START loginView here and get userName
    //val loginView:LoginView = new LoginView()
    val nick:String = args(0)
    val clientControl:ClientControl = new ClientControl(address, port)
    //TODO initialize controller with gui
    val control:Controller = new Controller(clientControl)
    clientControl.setupClient(nick)

    //val loginView = new LoginView
  }
}
