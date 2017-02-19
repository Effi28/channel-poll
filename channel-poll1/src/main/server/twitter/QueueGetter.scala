package main.server.twitter

import main.server.Server
import main.shared.data.Statement

final object QueueGetter {
  val dequeueThread = new Thread(() =>
    while (!Thread.currentThread().isInterrupted) {
      if (TwitterAccess.statementsQueue.nonEmpty) {
        val firstTweet:Statement = TwitterAccess.statementsQueue.dequeue()
        Server.broadcast(firstTweet)
      }
      Thread.sleep(7000)
    })
}
