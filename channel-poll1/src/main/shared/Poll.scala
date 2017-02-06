package main.shared

import scala.collection.mutable.HashMap

/**
  * Created by Brenda on 05.02.17.
  */
class Poll(val pollID: Int, val statementID: Long, val stamp: String, val userID: Long, val userName: String, val question: String, val options: HashMap[Int, (String, Int)]) {

}
