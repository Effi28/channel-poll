package main.shared.data

import scala.collection.mutable.HashMap

/**
  * Created by Brenda on 05.02.17.
  */
final class Poll(val pollID: Int, val statementID: Long, val stamp: String, val userID: Long, val userName: String, val question: String, val options: HashMap[Int, (String, Int)]) extends Serializable {

}
