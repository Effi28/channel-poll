package main.client

import java.net.URL
import java.io.{BufferedReader, InputStreamReader}

import twitter4j._
import javax.servlet.http.HttpServlet

import main.twitter.TwitterSettings
import twitter4j.auth.{AccessToken, RequestToken}


/**
  * Created by KathrinNetzer on 28.01.2017.
  */
class TwitterLogin() extends HttpServlet{



  def login(): Unit = {
    val TwitterSettings = new TwitterSettings
    val settings = TwitterSettings.settings

    try {
      val TwttrFctry: TwitterFactory = new TwitterFactory(settings.build())
      val myTwitter: Twitter = TwttrFctry.getInstance()

      try{
        val ReqToken: RequestToken = myTwitter.getOAuthRequestToken()
        var AccToken: AccessToken = null

        val BffrdRdr: BufferedReader = new BufferedReader(new InputStreamReader(System.in))
        while (null == AccToken) {
          try{
            val authUrl = new URL(ReqToken.getAuthorizationURL())

            return authUrl

          } catch {
            case e: TwitterException => println(e)
          }
        }
        val hasAccess = checkIfAccess(AccToken)
        println(hasAccess)
      }
      System.exit(0)
    }
  }


  def doLogin(loginCode: String): Unit = {
    if (loginCode.length > 0) {
      AccToken = myTwitter.getOAuthAccessToken(ReqToken, loginCode)
    }else {
      AccToken = myTwitter.getOAuthAccessToken(ReqToken)
    }

  }

  /**
    * Checks, if the user is authenticated.
    * @param AccToken  Oauth Access Token
    * @return true, if the user is authenticated
    */
  def checkIfAccess(AccToken: AccessToken): Boolean = {
    var ret = false
    if(AccToken.getToken != null && AccToken.getTokenSecret != null){
      ret = true
    }
    return ret
  }
}