package main.shared.data

/**
  * Created by KathrinNetzer on 05.02.2017.
  */
case class TwitterUser(val ID: Long, val userName: String) {

  override def toString: String = userName

}
