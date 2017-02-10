package main.shared.data

/**
  * Created by KathrinNetzer on 31.01.2017.
  */
final class Comment(val ID: Long, val statementID: Long, val userID: Long, val userName: String, val message: String, val timestamp: String) extends Serializable {

}
