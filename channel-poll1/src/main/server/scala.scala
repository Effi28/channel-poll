package main.server

import twitter4j.{TwitterStreamFactory, _}

import scala.collection.mutable.Queue
import scala.io.Source
import main.shared.Statement

/**
  * @author Kathrin Netzer
  */

// import scala.collection.mutable.ArrayBuffer

class TwitterAccess {

  // Put statements in a queue for the view
  var _statementsQueue = new Queue[Statement]
  def statementsQueue = this._statementsQueue
  def setStatementsQueue(twitterStatement: Statement): Unit = {
    this._statementsQueue += twitterStatement
  }

  // Settings for the registered application
  val TwitterSettings = new TwitterSettings
  val settings = TwitterSettings.streamsettings

  // Statements should be saved in JSON, done in classe SaveJsons
  val StatementsPath = TwitterSettings.pathToStatements
  val tweetsJsonString = Source.fromFile(StatementsPath).getLines.mkString
  val tweetsJson = new JSONObject(tweetsJsonString)
  val statements: JSONArray = tweetsJson.getJSONArray("data")
  val StatementsSaver = new SaveJsons(StatementsPath)

  val streamFactory = new TwitterStreamFactory(settings.build()).getInstance
  //streamFactory.addListener(getPosts(statements))
  streamFactory.addListener(getPosts(statements))
  val politicalFilter = new FilterQuery()

  //  follow - Specifies the users, by ID, to receive public tweets from.
  politicalFilter.follow(425845268, 47375691, 378693834, 569832889, 22260144, 888289790,
    304342650, 626287930, 16337664, 485751594, 347792540, 28066013, 46085533, 24725119,
    140821364, 293066780, 856641566, 113754382, 94363834, 18933321, 15943222, 299650387,
    17752770, 1425265488)
  //politicalFilter.filterLevel(TwitterSettings.filterLevel)
  //politicalFilter.language(TwitterSettings.filterLanguage)
  // politicalFilter.track(TwitterSettings.hashtags)
  //streamFactory.filter(politicalFilter)

  /**
    * Twitter Stream:
    * The Thread is important, because the Stream has to be running all the time.
    */

  val streamingThread = new Thread(new Runnable {
    override def run(): Unit = {
      // in periodical time intervals:  update json file
      var counter = 0
      println("**************************************")
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

  def getPosts(statements: JSONArray) = new StatusListener() {
    def onStatus(status: Status) {
      println(status.getUser)
      println(status.getText)
      // Create Json from tweetJson
      val tweetJson:JSONObject = new JSONObject()

      val message = status.getText
      val userid = status.getUser.getId
      val username = status.getUser.getName
      val screenname = status.getUser.getScreenName
      val profilepictureurl = status.getUser.getProfileImageURL
      val createdat = status.getCreatedAt.toString
      val tweetid = status.getId

      tweetJson.put("type", "statement")
      tweetJson.put("message", message)
      tweetJson.put("userid", userid)
      tweetJson.put("name", username)
      tweetJson.put("screenname", screenname)
      tweetJson.put("pictureurl", profilepictureurl)
      tweetJson.put("created_at", createdat)
      tweetJson.put("id", tweetid)

      // put Statement Object into the Queue
      val statementObject = new Statement(message, userid.toString, username, screenname,
        profilepictureurl, createdat, tweetid)
      setStatementsQueue(statementObject)
      statements.put(tweetJson)
    }

    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}
    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}
    def onException(ex: Exception) { ex.printStackTrace }
    def onScrubGeo(arg0: Long, arg1: Long) {}
    def onStallWarning(warning: StallWarning) {}
  }

}