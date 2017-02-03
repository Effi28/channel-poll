package main.client.view

class Statement(i: Int, t: String) {
  var id = i
  var text = t

  var likes = 0
  var comments = 0
  var polls = 0


  def setLikes (likes:Int): Unit = {
    this.likes = likes
  }

  def setComments(comments:Int): Unit ={
    this.comments = comments
  }

  def setPolls (polls: Int): Unit ={
    this.polls = polls
  }

}
