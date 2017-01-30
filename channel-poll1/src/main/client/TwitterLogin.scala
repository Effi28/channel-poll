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

  var a: AccessToken = null
  var r: RequestToken = null
  var m: Twitter = null

  def startLogin(): URL = {
    val TwitterSettings = new TwitterSettings
    val settings = TwitterSettings.settings

    try {
      val TwttrFctry: TwitterFactory = new TwitterFactory(settings.build())
      m = TwttrFctry.getInstance()

      try{
        r = m.getOAuthRequestToken()
        while (null == a) {
          try{
            val authUrl = new URL(r.getAuthorizationURL())
            return authUrl
          } catch {
            case e: TwitterException => println(e)
          }
        }
      }
    }
    return(new URL(""))
  }

  def doLogin(loginCode: String): Boolean = {
    val x = loginCode.length
    if (loginCode.length > 0) {
      println("1")
      a = m.getOAuthAccessToken(r, loginCode)
      println("2")
    }else {
      println("3")
      a = m.getOAuthAccessToken(r)
    }
    val hasAccess = checkIfAccess(a)
    println("..................................")
    println(hasAccess)
    return hasAccess
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
    println("ret:   " + ret)
    return ret
  }
}
