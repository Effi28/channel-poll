package client.model.clientCommunication

import org.json._
import main.shared.enums.JsonType

/**
  * Created by Effi2 on 16.01.2017.
  */
object ClientMessageBuilder {

  def LoginMessage(nick:String): JSONObject = {
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", JsonType.LOGIN)
    Jmsg.put("name", nick)
    Jmsg
  }
}
