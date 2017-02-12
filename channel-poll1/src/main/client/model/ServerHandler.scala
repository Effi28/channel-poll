package main.client.model

import main.shared._
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
    if (!ClientControl.pollAnswers.contains(pollAnswer.pollID)) {
      ClientControl.pollAnswers += pollAnswer.pollID -> new ObservableBuffer[PollAnswer]
    }
    ClientControl.pollAnswers.get(pollAnswer.pollID).get += pollAnswer
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
