package main.server.twitter

import main.server.Server

final object QueueGetter {
  val dequeueThread = new Thread(() =>
    while (!Thread.currentThread().isInterrupted) {
      if (TwitterAccess.statementsQueue.nonEmpty) {
        val firstTweet = TwitterAccess.statementsQueue.dequeue()
        Server.broadcastStatement(firstTweet)
      }
      Thread.sleep(7000)
    })
}
