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
  var a: AccessToken = null
  var r: RequestToken = null
  var m: Twitter = null

  def startLogin(): URL = {
    try {
      val TwttrFctry: TwitterFactory = new TwitterFactory(TwitterSettings.settings.build())
      m = TwttrFctry.getInstance()
      r = m.getOAuthRequestToken()
      while (null == a) {
        val authUrl = new URL(r.getAuthorizationURL())
        return authUrl
      }
    }
    catch {
      case e: TwitterException => println(e)
    }
    return (new URL(""))
  }

  def doLogin(loginCode: String): (Boolean, TwitterUser) = {
    var userScreenname = "noname"
    var userid: Long = 0
    val defUser: TwitterUser = new TwitterUser(userid, userScreenname)

    if (loginCode.length > 0) {
      a = m.getOAuthAccessToken(r, loginCode)
      userScreenname = m.getScreenName
      userid = m.getId
      val fullUser: TwitterUser = new TwitterUser(userid, userScreenname)
      val hasAccess = checkIfAccess(a)
      return (hasAccess, fullUser)
    } else {
      a = m.getOAuthAccessToken(r)
    }
    val hasAccess = checkIfAccess(a)
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
