package main.server

import java.io.BufferedReader

import main.shared.enums.JsonType
import main.shared.enums.JsonType.JsonType
import org.json.JSONObject

class ServerMessageReceiver(in:BufferedReader, client:ClientHandler) {

  def readMessage(): Unit ={
    var jsonText:String = null
    while (true) {
      if ((jsonText = in.readLine()) != null) {
        val jsonObject:JSONObject = new JSONObject(jsonText)
        println("SERVER RECEIVED: " + jsonObject)
        matchTest(JsonType.withName(jsonObject.optString("type")), jsonObject)
      }
    }
  }

  def matchTest(x: JsonType, jSONObject: JSONObject): Unit = x match {
    case JsonType.LOGIN => handleLogin(jSONObject)
    case JsonType.DISCONNECT => handleLogout(jSONObject)
    case JsonType.CHAT => handleChat(jSONObject)
    case JsonType.INVALIDMESSAGE => handleInvalid(jSONObject)
    case _ => handleInvalid(jSONObject)
  }

  def handleLogin(jSONObject: JSONObject): Unit ={
   val nick:String = jSONObject.optString("name")
    client.checkLogin(nick)
  }

  def handleLogout(jSONObject: JSONObject): Unit ={
    val nick:String = jSONObject.optString("name")
    client.handleLogout(nick)
  }


  def handleChat(jSONObject: JSONObject): Unit ={
    val sender:String =jSONObject.optString("senderID")
    val stamp:String =jSONObject.optString("stamp")
    val msg:String =jSONObject.optString("message")
    val rcv:String = jSONObject.optString("groupID")
    if(rcv == null){
      client.checkGlobalMessage(sender, stamp, msg)
    }
    else{
      client.checkGroupMessage(sender, stamp, msg, rcv)
    }
  }


  def handleInvalid(jSONObject: JSONObject): Unit ={

  }
}
