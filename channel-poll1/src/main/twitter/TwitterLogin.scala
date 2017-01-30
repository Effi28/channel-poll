package main.twitter
import java.net.URL
import java.io.{BufferedReader, InputStreamReader}
import twitter4j._
import javax.servlet.http.{HttpServlet}
import twitter4j.auth.{AccessToken, RequestToken}


/**
  * Created by KathrinNetzer on 28.01.2017.
  */
object TwitterLogin extends HttpServlet{

  def main(args: Array[String]): Unit = {
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
            println("Open the following URL and grant access to your account:")
            val authUrl = new URL(ReqToken.getAuthorizationURL())
            println(authUrl)
            println("Enter the PIN(if available) and hit enter after you granted access.[PIN]:");

            val str = BffrdRdr.readLine()
            println("********************************************************")
            println(str)

            if (str.length > 0) {
              AccToken = myTwitter.getOAuthAccessToken(ReqToken, str)
            }else {
              AccToken = myTwitter.getOAuthAccessToken(ReqToken)
            }
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
