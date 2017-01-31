package main.client.controller

import main.client.view.LoginView
import main.server.serverCommunication.ClientControl

/**
  * Created by Effi2 on 16.01.2017.
  */
object Controller{
  def sendMessage(msg:String):Unit={ClientControl.sendMessage(msg)}
  def sendMessage(msg:String, recv:String):Unit = {ClientControl.sendMessage(msg, recv)}


  def setupClient(nick1:String): Unit ={
    ClientControl.nick = nick1
    ClientControl.setupClient("localhost", 8008)
  }

  def closeLoginView(): Unit ={
    LoginView.close()
  }
}
