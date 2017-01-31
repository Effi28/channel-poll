package main.client.controller

import main.client.view.LoginView
import main.server.serverCommunication.ClientControl
import main.shared.Message

import scalafx.application.Platform

/**
  * Created by Effi2 on 16.01.2017.
  */
object Controller{
  def sendMessage(msg:Message):Unit={ClientControl.sendMessage(msg)}


  def setupClient(nick1:String): Unit ={
    ClientControl.nick = nick1
    ClientControl.setupClient("localhost", 8008)
  }

  def exitLoginView():Unit={
    Platform.runLater(new Runnable {
      override def run(): Unit = {
        LoginView.exit()
      }
    })
  }
}
