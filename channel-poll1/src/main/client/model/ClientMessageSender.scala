package main.client.model

import java.io.{BufferedWriter, OutputStreamWriter}

import main.shared.data.{Statement}
import main.shared.communication.MessageSender
import org.json.JSONObject
import org.slf4j.{Logger, LoggerFactory}


final object ClientMessageSender extends MessageSender{
  private val out: BufferedWriter = new BufferedWriter(new OutputStreamWriter(ClientControl.socket.getOutputStream, "UTF-8"))
  private val logger: Logger = LoggerFactory.getLogger(ClientMessageSender.getClass)

  def writeSubscribe(statement: Statement): Unit = {
    writeMessage(ClientMessageBuilder.writeSubscribe(ClientControl.user, statement))
  }

  def writeUnsubscribe(statement: Statement): Unit = {
    writeMessage(ClientMessageBuilder.writeUnsubscribe(ClientControl.user, statement))
  }

  def writeMessage(json: JSONObject): Unit = {
    logger.info(json.toString + "\n")
    out.write(json.toString + "\n")
    out.flush()
  }
}
