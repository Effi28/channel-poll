package client.model.clientCommunication

import java.io.OutputStreamWriter
import org.json._
/**
  * Created by Effi2 on 16.01.2017.
  */
class ClientMessageSender(out:OutputStreamWriter) {

  def writeLoginMessage(nick:String): Unit ={
    writeMessage(ClientMessageBuilder.LoginMessage(nick))
  }

  def writeMessage(json:JSONObject): Unit ={
    out.write(json.toString + "\n")
    out.flush()
    print(json.toString + "\n")
  }
}
