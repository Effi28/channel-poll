package main.server

import twitter4j.conf.ConfigurationBuilder

/**
  * Created by KathrinNetzer on 29.01.2017.
  */

class TwitterSettings {

  // Pathes to the Json Files
  val pathToStatements = "src/main/server/JsonFiles/Statements.json"
  val pathToComments = "src/main/server/JsonFiles/Comments.json"

  //"C:\\Users\\KathrinNetzer\\Desktop\\twitter\\src\\twitter\\Statements.json"


  // Login Oauth Settings
  val consumerKey = "wehGBs5c032ZcQz7elJG25RuB"
  val consumerSecret = "iPSm2vKsbt81ocCjxE8tyrYnQ5z4GiuegrmVdSaKzRI24yJQvl"

  // vorruebergehend (for streaming twitter appliction):
  val consumerKey1 = "DG7mPrtNYG4zkOWi31JC7ZZXH"
  val consumerSecret1 = "WBIzuPNnqNPIEAcHphdD1t0kILdLDRUwKNZOKEuPd85mI28HjH"

  val accessToken = "4874134271-uRS80fyMpmOUeUz4pKvyk077DHNcUTerKJjuzAm"
  val accessTokenSecret = "PRA5GqEsNDeL2uZ4MyB1qx0mINMO4istb6aHczQmy69Nf"

  val settings: ConfigurationBuilder = new ConfigurationBuilder
  settings.setDebugEnabled(true)
  settings.setOAuthConsumerKey(consumerKey)
  settings.setOAuthConsumerSecret(consumerSecret)

  val streamsettings: ConfigurationBuilder = new ConfigurationBuilder
  streamsettings.setDebugEnabled(true)
  streamsettings.setOAuthConsumerKey(consumerKey1)
  streamsettings.setOAuthConsumerSecret(consumerSecret1)
  streamsettings.setOAuthAccessToken(accessToken)
  streamsettings.setOAuthAccessTokenSecret(accessTokenSecret)

  // Settings for the Stream Filter
  //  follow - Specifies the users, by ID, to receive public tweets from.
  /**
  val interestingUsers = (425845268, 47375691, 378693834, 569832889, 22260144, 888289790,
    304342650, 626287930, 16337664, 485751594, 347792540, 28066013, 46085533, 24725119,
    140821364, 293066780, 856641566, 113754382, 94363834, 18933321, 15943222, 299650387,
    17752770, 1425265488)
  **/
  val hashtags = "cdu, csu, merkel, spdbpt, CSUListe, aft, grüne, fdp, linke, gabriel," +
    "steinmaier, zypries, bundestagswahl, merkel, angelamerkel, katjakipping," +
    "spitzenkandidat, spitzenkandidatin, linke_sachsen, wahlrecht, btw17, schulz," +
    "martinschulz, bundestag, deutschland, mega, kanzlerkandidatur, politik, germany," +
    "eu, osthessen, grünen, wahlumfrage, höcke, keinealternative, agenda, kraft," +
    "flüchtlinge, spdde, wahlbetrug, kanzleramt, mandate, kandidaten"
  val filterLanguage = "german"
  val filterLevel = "medium"
}