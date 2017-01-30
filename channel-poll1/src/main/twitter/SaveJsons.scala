package main.twitter

import java.io._
import twitter4j._
import scala.io.Source
import com.google.gson._

/**
  * Created by KathrinNetzer on 29.01.2017.
  */
class SaveJsons(path: String) {
  val jsonFile = new File(path)
  val gsonFormatter = new GsonBuilder().setPrettyPrinting().create
  val gsonParser = new JsonParser()

  def saveJsonString(jString: String): Unit = {

    var sString = jString
    val parsedStatementString = gsonParser.parse(sString)
    sString = gsonFormatter.toJson(parsedStatementString)
    val bufferedJsonWriter = new BufferedWriter(new FileWriter(jsonFile))
    bufferedJsonWriter.write(jString)
    bufferedJsonWriter.close()
  }

  /**
    *
    * @param statementID id of the statement the comment refers to
    * @param commenttext Comment text
    * @param username Screenname of the user that commented
    * @param likes [User Screenname]
    */
  def saveKomment(statementID: Int, commenttext: String, username: String, likes: Array[String]): Unit ={
    val commentsJsonString =  Source.fromFile(path).getLines.mkString
    val commentsJson = new JSONObject(commentsJsonString)
    val comments: JSONArray = commentsJson.getJSONArray("data")

    val commentJson: JSONObject = new JSONObject()
    commentJson.put("type", "comment")
    commentJson.put("message", commenttext)
    commentJson.put("screenname", username)
    commentJson.put("likes", likes)
    comments.put(commentJson)
    val commentssString = "{\"data\": " + comments.toString() + '}'
    saveJsonString(commentssString)
  }
}
