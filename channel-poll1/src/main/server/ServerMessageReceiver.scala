package main.server

import java.io.BufferedReader

/**
  * Created by Effi2 on 17.01.2017.
  */
class ServerMessageReceiver(in:BufferedReader) {

  def readMessage(): Unit ={
    var jsonText:String = null
    while (true) {
      if ((jsonText = in.readLine()) != null) {
        print(jsonText + "\n")
      }
    }
  }

}
