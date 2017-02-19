package main.shared.data

/**
  * Created by KathrinNetzer on 05.02.2017.
  */
final case class PollAnswer(val pollID: Long, val statementID: Long, val userID: Long, val userName: String, val question: String, val selectedOption: (Int, String),
                 val timestamp: String) extends Serializable {

}
