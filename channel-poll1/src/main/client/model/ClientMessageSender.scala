package main.client.model

import java.io.{BufferedWriter, OutputStreamWriter}
import main.shared.{Comment, Poll, Statement}
import org.json.JSONObject
import org.slf4j.{Logger, LoggerFactory}


final object ClientMessageSender{
  private val out:BufferedWriter = new BufferedWriter(new OutputStreamWriter(ClientControl.socket.getOutputStream, "UTF-8"))
  private val logger:Logger = LoggerFactory.getLogger(ClientMessageSender.getClass)

  def writeLoginMessage(): Unit ={
    writeMessage(ClientMessageBuilder.writeLogin(ClientControl.user.screenname))
  }

  def writeStComment(comment: Comment): Unit = {
    writeMessage(ClientMessageBuilder.writeComment(comment))
  }

  def writePoll(poll:Poll): Unit ={
    writeMessage(ClientMessageBuilder.writePoll(poll))
  }

  def writeLogout(): Unit ={
    writeMessage(ClientMessageBuilder.writeLogoutMessage(ClientControl.user.screenname))
    ClientControl.close()
  }

  def writeSubscribe(statement: Statement): Unit ={
    writeMessage(ClientMessageBuilder.writeSubscribe(ClientControl.user.screenname, statement))
  }

  def writeUnsubscribe(statement: Statement): Unit ={
    writeMessage(ClientMessageBuilder.writeUnsubscribe(ClientControl.user.screenname, statement))
  }

  private def writeMessage(json:JSONObject): Unit ={
    logger.info(json.toString + "\n")
    out.write(json.toString + "\n")
    out.flush()
  }
}
