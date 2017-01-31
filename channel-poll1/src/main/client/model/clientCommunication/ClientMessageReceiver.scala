package client.model.clientCommunication

import java.io.BufferedReader

import main.client.controller.Controller
import main.client.model.clientCommunication.ServerHandler
import main.shared.{Message, Statement}
import main.shared.enums.JsonType
import org.json.JSONObject

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

  def matchTest(x: JsonType.JsonType, jSONObject: JSONObject): Unit = x match {
    case JsonType.LOGINSUCCESS => handleLoginSuccessful(jSONObject)
    case JsonType.LOGINFAILED => handleLoginFailed(jSONObject)
    case JsonType.LOGIN => handleLogin(jSONObject)
    case JsonType.DISCONNECT => handleLogout(jSONObject)
    case JsonType.CHAT => handleChat(jSONObject)
    case JsonType.INVALIDMESSAGE => handleInvalid(jSONObject)
    case JsonType.STATEMENT => handleStatement(jSONObject)
    case _ => handleInvalid(jSONObject)
  }

  def handleLoginSuccessful(jSONObject:JSONObject): Unit ={
    Controller.exitLoginView()
  }

  def handleStatement(jSONObject: JSONObject): Unit = {
    val message: String = jSONObject.optString("message")
    val userID: String = jSONObject.optString("userid")
    val userName: String = jSONObject.optString("name")
    val screenName: String = jSONObject.optString("screenname")
    val pictureURL: String = jSONObject.optString("pictureurl")
    val creationDate: String = jSONObject.optString("created_at")
    val id: Int = jSONObject.optInt("id")
    handler.handleStatement(new Statement(message, userID, userName, screenName, pictureURL, creationDate, id))
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
    val id:Int = jSONObject.optInt("id")
    val rcv:String = jSONObject.optString("groupID")
    val msgT:Message = new Message(sender, stamp, msg, rcv, id)
    if(rcv == null){
      handler.handleGlobalChat(msgT)
    }
    else{
      handler.handleGroupChat(msgT)
    }
  }

  def handleInvalid(jSONObject: JSONObject):Unit = {
    //TODO
  }

}
