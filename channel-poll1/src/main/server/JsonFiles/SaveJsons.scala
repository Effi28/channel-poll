package main.server.JsonFiles

import java.io._
import com.google.gson._

import scala.collection.mutable.Queue
import org.json
import org.json.{JSONArray, JSONObject}

import scala.collection.mutable
import scala.io.Source
/**
  * Created by KathrinNetzer on 29.01.2017.
  */
final object SaveJsons {
  /**
    * Opens JSON- file, adds Objects and closes it
    */

  // pathes
  val pathToStatements = "src/main/server/JsonFiles/Statement.json"
  val pathToComments = "src/main/server/JsonFiles/Comments.json"
  val pathToPolls = "src/main/server/JsonFiles/Poll.json"
  val pathToPollAnswers = "src/main/server/JsonFiles/PollAnswer.json"

  val gsonFormatter = new GsonBuilder().setPrettyPrinting().create
  val gsonParser = new JsonParser()

  var statementsJsonQueue = new Queue[JSONObject]
  var commentsJsonQueue = new Queue[JSONObject]
  var pollsJsonQueue = new Queue[JSONObject]
  var pollsQueue = new mutable.Queue[JSONObject]

  def save_json(newJson: JSONObject) : Unit = {
    val jsonType: String = newJson.optString("type")
    matchRightFile(jsonType, newJson)
  }

  def matchRightFile(jsonType: String, newJSON: JSONObject) = jsonType match {
    case "POLL" =>
      val pollsString = Source.fromFile(pathToPolls).getLines.mkString
      val pollsJson = new JSONObject(pollsString)
      val polls: json.JSONArray = pollsJson.getJSONArray("data")
      polls.put(newJSON)
      val newPolls = createNewStr(polls)

    case "POLLANSWER" =>
      val pollAnswersString = Source.fromFile(pathToPollAnswers).getLines.mkString
      val pollAnswersJson = new JSONObject(pollAnswersString)
      val pollAnswers: json.JSONArray = pollAnswersJson.getJSONArray("data")
      pollAnswers.put(newJSON)
      val newPollAnswers = createNewStr(pollAnswers)

    case "COMMENT" =>
      val commentsString = Source.fromFile(pathToComments).getLines.mkString
      val commentsJson = new JSONObject(commentsString)
      val comments: json.JSONArray = commentsJson.getJSONArray("data")
      comments.put(newJSON)
      val newComments = createNewStr(comments)

    case "STATEMENT" =>
      val statementsString = Source.fromFile(pathToStatements).getLines.mkString
      val statementsJson = new JSONObject(statementsString)
      val statements: json.JSONArray = statementsJson.getJSONArray("data")
      statements.put(newJSON)
      val newStatements = createNewStr(statements)
  }

  def createNewStr(_jsonArray: JSONArray): String = {
    "{\"data\": " + _jsonArray.toString() + "}"
  }

  def finalSave(jsonFile:String, jString: String): Unit = {
    val parsedString = gsonParser.parse(jString)
    val gString = gsonFormatter.toJson(parsedString)
    val bufferedJsonWriter = new BufferedWriter(new FileWriter(jsonFile))
    bufferedJsonWriter.write(gString)
    bufferedJsonWriter.close()
  }
}

