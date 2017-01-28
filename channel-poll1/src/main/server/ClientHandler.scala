package main.server

import java.io.{BufferedReader, InputStreamReader, OutputStreamWriter}
import java.net.Socket

import client.model.clientCommunication.ClientMessageSender

class ClientHandler(socket:Socket, server:ServerControl) extends Runnable{
  val rec: ServerMessageReceiver = new ServerMessageReceiver(new BufferedReader(new InputStreamReader(socket.getInputStream, "UTF-8")), this)
  var sender:ServerMessageSender = new ServerMessageSender(new OutputStreamWriter(socket.getOutputStream, "UTF-8"))

  def message = (Thread.currentThread.getName() + "\n").getBytes

  def run(): Unit = {
    while (true) {
      rec.readMessage()
    }
  }

  def checkLogin(nick:String): Unit = {
    server.checkLogin(nick:String, this)
  }

  def checkGlobalMessage(sender:String, stamp:String, msg:String): Unit ={
    server.broadcastGlobalMessage(sender, stamp, msg)
  }

  def checkGroupMessage(sender:String, stamp:String, msg:String, rcv:String): Unit ={
    server.broadcastGroupMessage(sender, stamp, msg)
  }
}
