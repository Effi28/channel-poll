package client.model.clientCommunication

import play.api.libs.json.{JsString, JsValue, Json}

/**
  * Created by Effi2 on 16.01.2017.
  */
class ClientMessageBuilder {

  def writeLoginMessage(nick:String) {
    val json: JsValue = Json.parse("name" + nick);
  }
}
