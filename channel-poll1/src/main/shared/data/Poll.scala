package main.shared.data

import scala.collection.mutable.ArrayBuffer

/**
  * Created by Brenda on 05.02.17.
  */
final case class Poll(val ID: Long, val statementID: Long, val userID: Long, val userName: String, val question: String, val options: ArrayBuffer[Option], val timestamp: String) extends Serializable {

}
