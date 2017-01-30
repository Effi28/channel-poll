package main.twitter

import twitter4j._
import twitter4j.TwitterStreamFactory

import scala.collection.mutable.Queue
import scala.io.Source


/**
  * @author Kathrin Netzer
  */

// import scala.collection.mutable.ArrayBuffer

class TwitterAccess {

  // Put statements in a queue for the view
  var _statementsQueue = new Queue[JSONObject]
  def statementsQueue = this._statementsQueue
  def setStatementsQueue(twitterJson: JSONObject): Unit = {
    this._statementsQueue += twitterJson
  }

  // Settings for the registered application
  val TwitterSettings = new TwitterSettings
  val settings = TwitterSettings.settings

  // Statements should be saved in JSON, done in classe SaveJsons
  val StatementsPath = TwitterSettings.pathToStatements
  val tweetsJsonString = Source.fromFile(StatementsPath).getLines.mkString
  val tweetsJson = new JSONObject(tweetsJsonString)
  val statements: JSONArray = tweetsJson.getJSONArray("data")
  val StatementsSaver = new SaveJsons(StatementsPath)


  val streamFactory = new TwitterStreamFactory(settings.build()).getInstance
  //streamFactory.addListener(getPosts(statements))
  streamFactory.addListener(getPosts())
  val politicalFilter = new FilterQuery()

  //  follow - Specifies the users, by ID, to receive public tweets from.
  politicalFilter.follow()
  politicalFilter.track(TwitterSettings.hashtags)
  politicalFilter.filterLevel(TwitterSettings.filterLevel)
  politicalFilter.language(TwitterSettings.filterLanguage)
  streamFactory.filter(politicalFilter)

  /**
    * Twitter Stream:
    * The Thread is important, because the Stream has to be running all the time.
    */

  val streamingThread = new Thread(new Runnable {
    override def run(): Unit = {
      // in periodical time intervals:  update json file
      var counter = 0
      while (!Thread.currentThread().isInterrupted()) {
        if(counter < 3){
          counter += 1
        }else{
          counter = 0
          // update Json File: Write statements to file
          val statementsString = "{\"data\": " + statements.toString() + '}'
          StatementsSaver.saveJsonString(statementsString)
        }

        Thread.sleep(10000) // important: not too low!
        streamFactory.cleanUp()
        streamFactory.shutdown()
    }}
  })
  streamingThread.start()

  //def getPosts(statements: JSONArray) = new StatusListener() {

  def getPosts() = new StatusListener {
    def onStatus(status: Status) {
      // Create Json from tweetJson
      val tweetJson:JSONObject = new JSONObject()
      tweetJson.put("type", "statement")
      tweetJson.put("message", status.getText)
      tweetJson.put("userid", status.getUser.getId)
      tweetJson.put("name", status.getUser.getName)
      tweetJson.put("screenname", status.getUser.getScreenName)
      tweetJson.put("pictureurl", status.getUser.getProfileImageURL)
      tweetJson.put("created_at", status.getCreatedAt.toString)
      tweetJson.put("id", status.getId)
      // put Json Statement into the Queue
      setStatementsQueue(tweetJson)
      statements.put(tweetJson)

    }

    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}
    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}
    def onException(ex: Exception) { ex.printStackTrace }
    def onScrubGeo(arg0: Long, arg1: Long) {}
    def onStallWarning(warning: StallWarning) {}
  }

}