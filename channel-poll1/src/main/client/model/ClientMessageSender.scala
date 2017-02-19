package main.client.model

import java.io.{BufferedWriter, OutputStreamWriter}

import main.shared.data.{Statement}
import main.shared.communication.MessageSender
import org.json.JSONObject
import org.slf4j.{Logger, LoggerFactory}


final object ClientMessageSender extends MessageSender{
  private val out = new BufferedWriter(new OutputStreamWriter(ClientControl.socket.getOutputStream, "UTF-8"))
  private val logger = LoggerFactory.getLogger(ClientMessageSender.getClass)


  def writeMessage(json: JSONObject) = {
    logger.info(json.toString + "\n")
    out.write(json.toString + "\n")
    out.flush
  }
}
