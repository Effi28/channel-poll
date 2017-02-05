package main.shared

import scala.collection.mutable.HashMap

/**
  * Created by Brenda on 05.02.17.
  */
class Poll(val statementID: Long, stamp: String, user: String, val question: String, val options: HashMap[Int, (String, Int)]) {

}
