package twitter

/**
  * @author Kathrin Netzer
  */
object TweetGetter {
  def main(args: Array[String]): Unit ={
    val TestTwitterAccess = new TwitterAccess()
    val dequeueThread = new Thread(new Runnable {
      override def run(): Unit = {
        while (!Thread.currentThread().isInterrupted){
          println(TestTwitterAccess.statementsQueue)
          if(TestTwitterAccess.statementsQueue.nonEmpty) {
            val firstTweet = TestTwitterAccess.statementsQueue.dequeue()
          }
          Thread.sleep(3000)
        }
      }
    })
  dequeueThread.start()
  }
}
