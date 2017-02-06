package main.shared

/**
  * Created by KathrinNetzer on 05.02.2017.
  */
class PollAnswer(val userID: Long, val userName: String, val question: String, val selectedOption: (Int, String), val pollID: Int,
                 val timestamp: String, val statementID: Long) {

}
