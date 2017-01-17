package client.model.clientCommunication

import java.io.OutputStreamWriter
import java.net.Socket

import org.json.JSONObject

/**
  * Created by Effi2 on 16.01.2017.
  */
class ClientMessageSender {

  def writeMessage(socket: Socket, msg: String): Unit ={
    val out: OutputStreamWriter = new OutputStreamWriter(socket.getOutputStream, "UTF-8")
    val Jmsg:JSONObject = new JSONObject()
    Jmsg.put("type", "name")
    Jmsg.put("name", msg)
    out.write(Jmsg.toString + "\n")
    out.flush()
    print(Jmsg.toString + "\n")
  }
}
