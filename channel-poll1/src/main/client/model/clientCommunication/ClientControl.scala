package main.server.serverCommunication

import java.net.Socket

import client.model.clientCommunication.{ClientMessageReceiver, ClientMessageSender}

import scala.collection.mutable.MutableList
import scala.collection.mutable

/**
  * Created by Effi2 on 16.01.2017.
  */
class ClientControl() {
  val nick:String = "123"
  val users:MutableList[String] = new mutable.MutableList[String];

  def setupClient(address:String, port:Int): Unit ={
    val socket:Socket = new Socket(address, port)
    val thread = new Thread{
      override def run: Unit ={
        val rec:ClientMessageReceiver = new ClientMessageReceiver()
        while(!Thread.interrupted()) {
        rec.readMessage(socket)
        }
        socket.close()
      }
    }
    val sender:ClientMessageSender = new ClientMessageSender()
    while(true){
      sender.writeMessage(socket, nick)
      Thread.sleep(1000)
    }
  }
}