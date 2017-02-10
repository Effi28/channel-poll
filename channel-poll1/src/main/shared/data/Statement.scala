package main.shared.data

final class Statement(val ID: Long, val userID: Long, val userName: String, val pictureURL: String, val message: String, val timestamp: String) extends Serializable {

}
