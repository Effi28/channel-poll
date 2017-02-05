package main.server

import main.shared.Statement

/**
  * @author Kathrin Netzer
  */
class QueueGetter {
  val TestTwitterAccess = new TwitterAccess()

  val dequeueThread = new Thread(new Runnable {
    override def run(): Unit = {
      while (!Thread.currentThread().isInterrupted){
        println(TestTwitterAccess.statementsQueue)
        if(TestTwitterAccess.statementsQueue.nonEmpty) {
          val firstTweet = TestTwitterAccess.statementsQueue.dequeue()
          Server.addStatement(firstTweet)
        }
        Thread.sleep(7000)
      }
    }
  })
}
