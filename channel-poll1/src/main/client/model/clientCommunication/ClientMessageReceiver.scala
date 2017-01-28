package client.model.clientCommunication

import java.io.BufferedReader
/**
  * Created by Effi2 on 16.01.2017.
  */
class ClientMessageReceiver(in:BufferedReader) {

  def readMessage(): Unit ={
    var jsonText:String = null
    while (true) {
      if ((jsonText = in.readLine()) != null) {
        println("CLIENT RECEIVED: "+ jsonText + "\n")
      }
    }
  }
}
