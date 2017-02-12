package main.client.model

import main.shared.data.{Comment, Poll, PollAnswer, Statement}

import scalafx.collections.ObservableBuffer


final object ServerHandler extends Thread {
  def message = (Thread.currentThread.getName() + "\n").getBytes

  override def run(): Unit = {
    while (true) {
      ClientMessageReceiver.readMessage()
    }
  }

  def handleLogin(nick: String): Unit = {
    ClientControl.users += nick
  }

  def handleLogout(nick: String): Unit = {
    ClientControl.users -= nick
  }

  def handlePoll(poll: Poll): Unit = {
    if (!ClientControl.polls.contains(poll.statementID)) {
      ClientControl.polls += poll.statementID -> new ObservableBuffer[Poll]

    }
    ClientControl.polls.get(poll.statementID).get += poll
  }

  def handlePollAnswer(pollAnswer: PollAnswer): Unit = {
    for(poll <- ClientControl.polls.get(pollAnswer.statementID).get){
      if(poll.ID == pollAnswer.pollID){
        for(option <- poll.options){
          if(option.key == pollAnswer.selectedOption._1){
            option.likes += 1
            println (option.name +" has: "  + option.likes + " likes.")
          }
        }
      }
    }
  }

  def handleStatement(statement: Statement): Unit = {
    ClientControl.statements += statement
  }

  def handleComment(comment: Comment): Unit = {
    if (!ClientControl.comments.contains(comment.statementID)) {
      ClientControl.comments += comment.statementID -> new ObservableBuffer[Comment]()

    }
    ClientControl.comments.get(comment.statementID).get += comment
  }
}
