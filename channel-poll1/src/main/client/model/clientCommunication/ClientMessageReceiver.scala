package client.model.clientCommunication

import java.io.{BufferedReader, InputStreamReader}
import main.client.controller.Controller
import main.client.model.clientCommunication.ServerHandler
import main.server.serverCommunication.ClientControl
import main.shared.{Message, Statement, Comment}
import main.shared.enums.JsonType
import org.json.{JSONArray, JSONObject}


object ClientMessageReceiver {
  val in:BufferedReader = new BufferedReader(new InputStreamReader(ClientControl.socket.getInputStream, "UTF-8"))

  def readMessage(): Unit ={
    var jsonText:String = null
    while (!ClientControl.socket.isClosed) {
      if ((jsonText = in.readLine()) != null) {
        val jsonObject: JSONObject = new JSONObject(jsonText)
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


  def handleLoginSuccessful(json:JSONObject): Unit ={
    val userNames: JSONArray = json.optJSONArray("users")
    for(i <- 0 until userNames.length()){
      ServerHandler.handleLogin(userNames.getString(i))
    }
    Controller.exitLoginView()
  }

  def handleStatement(json: JSONObject): Unit = {
    val message: String = json.optString("message")
    val userID: String = json.optString("userid")
    val userName: String = json.optString("name")
    val screenName: String = json.optString("screenname")
    val pictureURL: String = json.optString("pictureurl")
    val creationDate: String = json.optString("created_at")
    val id: Int = json.optInt("id")
    ServerHandler.handleStatement(new Statement(message, userID, userName, screenName, pictureURL, creationDate, id))
  }


  def handleComment(json: JSONObject): Unit = {

    val statement:Statement = getStatement(json.optJSONObject("statement"))

    ServerHandler.handleComment(new Comment(statement, json.optString("message"),json.optString("screenname"), json.optInt("id")))
  }

  def getStatement(json: JSONObject): Statement = {
    val jsonStatement:JSONObject = json.optJSONObject("statement")
    val message: String = jsonStatement.optString("message")
    val userID: String = jsonStatement.optString("userid")
    val userName: String = jsonStatement.optString("name")
    val screenName: String = jsonStatement.optString("screenname")
    val pictureURL: String = jsonStatement.optString("pictureurl")
    val creationDate: String = jsonStatement.optString("created_at")
    val id: Int = jsonStatement.optInt("id")
    new Statement(message, userID, userName, screenName, pictureURL, creationDate, id)
  }

  def handlePollAnswer(jSONObject: JSONObject): Unit = {
    ServerHandler.handlePollAns
  }


    def handleLoginFailed(jSONObject:JSONObject): Unit ={
    //TODO show the reason why login failed(e.G. username already taken)
  }

  def handleLogin(jSONObject:JSONObject): Unit ={
    ServerHandler.handleLogin(jSONObject.optString("name"))
  }

  def handleLogout(jSONObject:JSONObject): Unit ={
    ServerHandler.handleLogout(jSONObject.optString("name"))
  }

  def handleChat(jSONObject:JSONObject): Unit = {
    val msg:Message = new Message(jSONObject.optString("senderID"), jSONObject.optString("stamp"), jSONObject.optString("message"), jSONObject.optString("groupID"), jSONObject.optInt("id"))
    if(msg.rcv == null){
      ServerHandler.handleGlobalChat(msg)
    }
    else{
      ServerHandler.handleGroupChat(msg)
    }
  }

  def handleInvalid(jSONObject: JSONObject):Unit = {
  }
}
