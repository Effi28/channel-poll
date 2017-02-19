package main.server.twitter

import twitter4j.conf.ConfigurationBuilder

/**
  * Created by KathrinNetzer on 29.01.2017.
  */

final object TwitterSettings {

  // Login Oauth Settings
  val consumerKey = "wehGBs5c032ZcQz7elJG25RuB"
  val consumerScrt = "iPSm2vKsbt81ocCjxE8tyrYnQ5z4GiuegrmVdSaKzRI24yJQvl"

  // vorruebergehend (for streaming twitter appliction):
  val consumerKey1 = "DG7mPrtNYG4zkOWi31JC7ZZXH"
  val consumerScrt1 = "WBIzuPNnqNPIEAcHphdD1t0kILdLDRUwKNZOKEuPd85mI28HjH"

  val accessToken = "4874134271-uRS80fyMpmOUeUz4pKvyk077DHNcUTerKJjuzAm"
  val accessTokenScrt = "PRA5GqEsNDeL2uZ4MyB1qx0mINMO4istb6aHczQmy69Nf"

  val usersettings: ConfigurationBuilder = new ConfigurationBuilder
  usersettings.setDebugEnabled(true)
  usersettings.setOAuthConsumerKey(consumerKey)
  usersettings.setOAuthConsumerSecret(consumerScrt)

  val streamsettings: ConfigurationBuilder = new ConfigurationBuilder
  streamsettings.setDebugEnabled(true)
  streamsettings.setOAuthConsumerKey(consumerKey1)
  streamsettings.setOAuthConsumerSecret(consumerScrt1)
  streamsettings.setOAuthAccessToken(accessToken)
  streamsettings.setOAuthAccessTokenSecret(accessTokenScrt)

  // Settings for the Stream Filter

  val tracks = "cdu,csu,merkel,spdbpt,CSUListe,aft,die grünen,fdp,linke,gabriel," +
    "steinmaier,zypries,bundestagswahl,merkel,angelamerkel,katjakipping," +
    "spitzenkandidat,spitzenkandidatin,linke_sachsen,wahlrecht,btw17,Martin Schulz," +
    "martinschulz,bundestag,deutschland,mega politik,kanzlerkandidatur,politik,germany," +
    "eu,osthessen,wahlumfrage,höcke,keinealternative,agenda,politik kraft," +
    "flüchtlinge,spdde,wahlbetrug,kanzleramt,mandate,kandidaten," +
    "wahl,bundestagsfraktion,bundestagswahl,duisburger,duisburg,tormentaelectrica," +
    "germany,gewittersturm,politik,journalism,Politik,Agenda,Höcke," +
    "Wahlumfrage,Flüchtlinge,EU,Zypries," +
    "volkerpispers,erstdenkendannwählen,wahl,1933,partei,2016," +
    "wahlkampf,campaign,neue jobs,igersberlin,berlin,travel,guardiancities," +
    "grüne partei,deutsche jobs,angela merkel,wahl berlin,erst denken dann wählen," +
    "diepartei,partei,die partei,seehofer,schröder,Sigmar Gabriel," +
    "Brigitte Zypries,Thomas de Maiziere,Heiko Maas,Andrea Nahles," +
    "Christian Schmidt,Ursula von der Leyen,Manuela Schwesig," +
    "Hermann Gröhe,Barbara Hendricks,Johanna Wanka,Gerd Müller,Peter Altmeier"

  val filterLanguage = "de"
}