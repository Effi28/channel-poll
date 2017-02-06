package main.shared.enums

import scala.collection.immutable.HashMap

/**
  * Created by Effi2 on 17.01.2017.
  */
object JsonType extends Enumeration {
  type JsonType = Value
  val LOGIN = Value("login")
  val LOGINFAILED = Value("login failed")
  val LOGINSUCCESS = Value("login success")
  val DISCONNECT = Value("disconnect")
  val ACKNOWLEDGEDISCONNECT = Value("acknowledge disconnect")
  val INVALIDMESSAGE = Value("invalid message")
  val CHAT = Value("chat")
  val STATEMENT = Value("statement")
  val COMMENT = Value("comment")
  val SUBSCRIBE = Value("subscribe")
  val UNSUBSCRIBE = Value("unsubscribe")
  val POLL = Value("poll")
  val POLLANSWER = Value("pollanswer")
}
