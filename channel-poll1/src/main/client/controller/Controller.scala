package main.client.controller

import main.server.serverCommunication.ClientControl


/**
  * Created by Effi2 on 16.01.2017.
  */
class Controller (client: ClientControl){

  def sendMessage(msg:String):Unit={client.sendMessage(msg)}
  def sendMessage(msg:String, recv:String):Unit = {client.sendMessage(msg, recv)}
}
