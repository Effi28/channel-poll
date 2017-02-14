package main.server.twitter

import main.shared.data.Statement
import twitter4j.{TwitterStreamFactory, _}

import scala.collection.mutable.Queue

object TwitterAccess {

  // Settings for the registered application
  val settings = TwitterSettings.streamsettings
  val streamFactory = new TwitterStreamFactory(settings.build()).getInstance
  val politicalFilter = new FilterQuery()
  /**
    * Twitter Stream:
    * The Thread is important, because the Stream has to be running all the time.
    */

  val streamingThread = new Thread(() => {
    while (!Thread.currentThread().isInterrupted()) {
      Thread.sleep(10000) // important: not too low!
      streamFactory.cleanUp()
      streamFactory.shutdown()
    }
  })
  // Put statements in a queue for the view
  var _statementsQueue = new Queue[Statement]
  streamFactory.addListener(getPosts())

  def statementsQueue = this._statementsQueue

  //  follow - Specifies the users, by ID, to receive public tweets from.
  //politicalFilter.follow(425845268, 47375691, 378693834, 569832889, 22260144, 888289790,
  //  304342650, 626287930, 16337664, 485751594, 347792540, 28066013, 46085533, 24725119,
  //  140821364, 293066780, 856641566, 113754382, 94363834, 18933321, 15943222, 299650387,
  //  17752770, 1425265488)
  //politicalFilter.filterLevel(TwitterSettings.filterLevel)
  politicalFilter.language(TwitterSettings.filterLanguage)
  politicalFilter.track(TwitterSettings.hashtags)
  streamFactory.filter(politicalFilter)

  def getPosts() = new StatusListener() {
    def onStatus(nextStatus: Status) {
      // Create Json from tweetJson
      println(nextStatus)
      val message = nextStatus.getText
      val userid = nextStatus.getUser.getId
      val username = nextStatus.getUser.getName
      val screenname = nextStatus.getUser.getScreenName
      val profilepictureurl = nextStatus.getUser.getProfileImageURL
      val createdat = nextStatus.getCreatedAt.toString
      val tweetid = nextStatus.getId

      // put Statement Object into the Queue
      val statementObject = new Statement(tweetid, userid, username,
        profilepictureurl, message, createdat)
      setStatementsQueue(statementObject)
    }

    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}

    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}

    def onException(ex: Exception) {
      ex.printStackTrace
    }

    def onScrubGeo(arg0: Long, arg1: Long) {}

    def onStallWarning(warning: StallWarning) {}
  }

  def setStatementsQueue(twitterStatement: Statement): Unit = {
    this._statementsQueue += twitterStatement
  }
}