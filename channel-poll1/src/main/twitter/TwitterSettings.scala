package main.twitter
import twitter4j.conf.ConfigurationBuilder

/**
  * Created by KathrinNetzer on 29.01.2017.
  */

class TwitterSettings {

  // Pathes to the Json Files
  val pathToStatements = "src/main/twitter/Statements.json"
  val pathToComments = "src/main/twitter/Comments.json"

  //"C:\\Users\\KathrinNetzer\\Desktop\\twitter\\src\\twitter\\Statements.json"


  // Login Oauth Settings
  val consumerKey = "wehGBs5c032ZcQz7elJG25RuB"
  val consumerSecret = "iPSm2vKsbt81ocCjxE8tyrYnQ5z4GiuegrmVdSaKzRI24yJQvl"
  val accessToken = "4874134271-uRS80fyMpmOUeUz4pKvyk077DHNcUTerKJjuzAm"
  val accessTokenSecret = "PRA5GqEsNDeL2uZ4MyB1qx0mINMO4istb6aHczQmy69Nf"

  val settings: ConfigurationBuilder = new ConfigurationBuilder
  settings.setDebugEnabled(true)
  settings.setOAuthConsumerKey(consumerKey)
  settings.setOAuthConsumerSecret(consumerSecret)

  val streamsettings: ConfigurationBuilder = new ConfigurationBuilder
  streamsettings.setDebugEnabled(true)
  streamsettings.setOAuthConsumerKey(consumerKey)
  streamsettings.setOAuthConsumerSecret(consumerSecret)
  streamsettings.setOAuthAccessToken(accessToken)
  streamsettings.setOAuthAccessTokenSecret(accessTokenSecret)

  // Settings for the Stream Filter
  //  follow - Specifies the users, by ID, to receive public tweets from.
  val interestingUsers = (25073877, 813286, 1339835893, 23022687, 939091, 18916432,
    15808765, 36412963, 36042554, 14224719, 24705126, 20713061, 15416505,
    65493023, 733751245, 490126636, 19394188, 34996429, 7334402, 12044602,
    15762708, 28587919, 16832632, 138787319, 135815730, 121887978, 113420831,
    101782947, 78431501, 82939583, 70188601, 65691824, 62442994, 55235265,
    50982086, 45835766, 44335525, 33576489)
  val hashtags = "elections, politics, trump, america, democrats," +
    " obama, republicans"
  val filterLanguage = "en"
  val filterLevel = "medium"
}