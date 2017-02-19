package main.server.twitter

import main.shared.data.Statement
import twitter4j.{TwitterStreamFactory, _}

import scala.collection.mutable.Queue

object TwitterAccess {

  // Settings for the registered application
  val settings = TwitterSettings.streamsettings
  val liveStats = new TwitterStreamFactory(settings.build()).getInstance
  val politicalFilter = new FilterQuery()

  //Twitter Stream:
  //The Thread is important, because the Stream has to be running all the time.

  val streamingThread = new Thread(() => {
    while (!Thread.currentThread().isInterrupted()) {
      Thread.sleep(10000) // important: not too low!
      liveStats.cleanUp()
      liveStats.shutdown()
    }
  })

  // Put statements in a queue for the view
  var _statementsQueue = new Queue[Statement]
  liveStats.addListener(getPosts())

  def statementsQueue = this._statementsQueue

  //  follow - Specifies the users, by ID, to receive public tweets from.


  politicalFilter.follow(425845268, 47375691, 378693834, 569832889, 22260144, 888289790,
     304342650, 626287930, 16337664, 485751594, 347792540, 28066013, 46085533, 24725119,
     140821364, 293066780, 856641566, 113754382, 94363834, 18933321, 15943222, 299650387,
     17752770, 1425265488, 139407967, 16255941, 22900494, 20877217,
     19257571, 35687457, 17229513, 16674128, 83342791, 18866407,
     429357796, 1114675538, 37105136, 35846585, 28525166,
     560467206, 73088949, 17343047, 52024480, 53351462, 409203792,
     22125770, 111054843, 110368456, 52119203, 42698498,
     281941547, 1131092102, 87281380, 569166756, 36327895,
     51752254, 60630139, 1423622834, 17870195, 277006619,
     332698790, // politicans until here
     40227292)

  //politicalFilter.follow(2548035848L)
  politicalFilter.language(TwitterSettings.filterLanguage)
  politicalFilter.track(TwitterSettings.hashtags)
  liveStats.filter(politicalFilter)

  def getPosts() = new StatusListener() {
    def onStatus(nextStatus: Status) {
      // Create Json from tweetJson
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