package main.server

import java.io.BufferedReader

import play.libs.Json


/**
  * Created by Effi2 on 16.01.2017.
  */
class ServerMessageReceiver {

  def readMessage(in:BufferedReader): Unit ={
    var jsonText:String = "";
    if((jsonText = in.readLine()) != null){
    print (jsonText)
    }
  }
}
