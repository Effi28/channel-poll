package main.client.view

import java.net.URL
import javax.servlet.http.HttpServlet

import main.server.twitter.TwitterSettings
import main.shared.data.TwitterUser
import twitter4j._
import twitter4j.auth.{AccessToken, RequestToken}

/**
  * Created by KathrinNetzer on 28.01.2017.
  */
final object TwitterLogin extends HttpServlet {
  var accssTkn: AccessToken = null
  var rqstTkn: RequestToken = null
  var twtr: Twitter = null

  def startLogin(): URL = {
    try {
      val TwttrFctry: TwitterFactory = new TwitterFactory(TwitterSettings.usersettings.build())
      twtr = TwttrFctry.getInstance()
      rqstTkn = twtr.getOAuthRequestToken()
      while (null == accssTkn) {
        val userAuthUrl = new URL(rqstTkn.getAuthorizationURL())
        return userAuthUrl
      }
    }
    catch {
      case e: TwitterException => println(e)
    }
    return (new URL(""))
  }

  def doLogin(loginCode: String): (Boolean, TwitterUser) = {
    var username = "noname"
    var userid: Long = 0
    val defUser: TwitterUser = new TwitterUser(userid, username)

    if (loginCode.length > 0) {
      accssTkn = twtr.getOAuthAccessToken(rqstTkn, loginCode)
      username = twtr.getScreenName
      userid = twtr.getId
      val fullUser: TwitterUser = new TwitterUser(userid, username)
      val hasAccess = checkIfAccess(accssTkn)
      return (hasAccess, fullUser)
    } else {
      accssTkn = twtr.getOAuthAccessToken(rqstTkn)
    }
    val hasAccess = checkIfAccess(accssTkn)
    return (hasAccess, defUser)
  }

  /**
    * Checks, if the user is authenticated.
    *
    * @param AccToken Oauth Access Token
    * @return true, if the user is authenticated
    */
  private def checkIfAccess(AccToken: AccessToken): Boolean = {
    var ret = false
    if (AccToken.getToken != null && AccToken.getTokenSecret != null) {
      ret = true
    }
    return ret
  }
}
