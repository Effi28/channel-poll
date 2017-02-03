package client.model.clientCommunication

import java.io.BufferedReader

import main.client.controller.Controller
import main.client.model.clientCommunication.ServerHandler
import main.client.view.{ClientView, LoginView}
import main.shared.enums.JsonType
import main.shared.enums.JsonType.JsonType
import org.json.JSONObject

import scalafx.application.Platform

class ClientMessageReceiver(in:BufferedReader, handler:ServerHandler) {

  def readMessage(): Unit ={
    var jsonText:String = null
    while (true) {
      if ((jsonText = in.readLine()) != null) {
        val jsonObject:JSONObject = new JSONObject(jsonText)
        println("CLIENT RECEIVED: " + jsonObject)
        matchTest(JsonType.withName(jsonObject.optString("type")), jsonObject)
      }
    }
  }

  def matchTest(x: JsonType, jSONObject: JSONObject): Unit = x match {
    case JsonType.LOGINSUCCESS => handleLoginSuccessful(jSONObject)
    case JsonType.LOGINFAILED => handleLoginFailed(jSONObject)
    case JsonType.LOGIN => handleLogin(jSONObject)
    case JsonType.DISCONNECT => handleLogout(jSONObject)
    case JsonType.CHAT => handleChat(jSONObject)
    case JsonType.INVALIDMESSAGE => handleInvalid(jSONObject)
    case _ => handleInvalid(jSONObject)
  }

  def handleLoginSuccessful(jSONObject:JSONObject): Unit ={
    Platform.runLater(new Runnable {
      override def run(): Unit = {
        LoginView.exit()
      }
    })
  }

  def handleStatement(jSONObject: JSONObject):Unit={

  }

  def handleLoginFailed(jSONObject:JSONObject): Unit ={
    //TODO show the reason why login failed(e.G. username already taken)
  }

  def handleLogin(jSONObject:JSONObject): Unit ={
    val nick:String = jSONObject.optString("name")
    handler.handleLogin(nick)
  }

  def handleLogout(jSONObject:JSONObject): Unit ={
    val nick:String = jSONObject.optString("name")
    handler.handleLogout(nick)
  }

  def handleChat(jSONObject:JSONObject): Unit = {
    val sender:String =jSONObject.optString("senderID")
    val stamp:String =jSONObject.optString("stamp")
    val msg:String =jSONObject.optString("message")
    val rcv:String = jSONObject.optString("groupID")
    if(rcv == null){
    }
    else{
    }
    //TODO show the chat message in GUI
  }

  def handleInvalid(jSONObject: JSONObject):Unit = {
    //TODO
  }

}
