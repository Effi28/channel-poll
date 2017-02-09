package server

import main.server.Server

final object QueueGetter {
  val dequeueThread = new Thread(() =>
    while (!Thread.currentThread().isInterrupted) {
      println(TwitterAccess.statementsQueue)
      if (TwitterAccess.statementsQueue.nonEmpty) {
        val firstTweet = TwitterAccess.statementsQueue.dequeue()
        Server.broadcastStatement(firstTweet)
      }
      Thread.sleep(7000)
    })
}
