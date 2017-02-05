package main.shared

/**
  * Created by KathrinNetzer on 05.02.2017.
  */
class PollAnswer(val userid: Long, val question: String, val selectedOption: (Int, String), val pollID: Int,
                 val timestamp: String, val statementID: Long) {

}
