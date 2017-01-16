package client.model.clientCommunication

import java.io.OutputStreamWriter
import java.net.Socket

/**
  * Created by Effi2 on 16.01.2017.
  */
class ClientMessageSender {

  def writeMessage(socket: Socket, msg: String): Unit ={
    val out: OutputStreamWriter = new OutputStreamWriter(socket.getOutputStream, "UTF-8")
    print(msg)
    out.write(msg)
    out.flush()
  }
}
