package main.server.JsonFiles

import java.io._

import com.google.gson._
import org.json
import org.json.{JSONArray, JSONObject}

import scala.collection.mutable.Queue
import scala.io.Source

/**
  * Created by KathrinNetzer on 29.01.2017.
  */
final object SaveJsons {

  var statementsJsonQueue = new Queue[JSONObject]
  var commentsJsonQueue = new Queue[JSONObject]
  var pollsAnswersQueue = new Queue[JSONObject]
  var pollsQueue = new Queue[JSONObject]
  var writeStatementsFlag = false
  var writeCommentsFlag = false
  var writePollAnswersFlag = false
  var writePollsFlag = false

  val saveJsonsThread = new Thread(() =>
    while (!Thread.currentThread().isInterrupted) {
      while (statementsJsonQueue.nonEmpty && !writeStatementsFlag) {
        val firstStatement = statementsJsonQueue.dequeue()
        save_json(firstStatement)
      }
      while (commentsJsonQueue.nonEmpty && !writeStatementsFlag) {
        val firstComment = commentsJsonQueue.dequeue()
        save_json(firstComment)
      }
      while (pollsAnswersQueue.nonEmpty && !writePollAnswersFlag) {
        val firstPollAnswer = pollsAnswersQueue.dequeue()
        save_json(firstPollAnswer)
      }
      while (pollsQueue.nonEmpty && !writePollsFlag) {
        val firstPoll = pollsQueue.dequeue()
        save_json(firstPoll)
      }
      Thread.sleep(7000)
    })
  /**
    * Opens JSON- file, adds Objects and closes it
    */

  // pathes
  private val pathToStatements = "src/main/server/JsonFiles/Statement.json"
  private val pathToComments = "src/main/server/JsonFiles/Comments.json"
  private val pathToPolls = "src/main/server/JsonFiles/Poll.json"
  private val pathToPollAnswers = "src/main/server/JsonFiles/PollAnswer.json"
  private val gsonFormatter = new GsonBuilder().setPrettyPrinting().create
  private val gsonParser = new JsonParser()

  private def save_json(newJson: JSONObject): Unit = {
    val jsonType: String = newJson.optString("type")
    println(jsonType)
    matchRightFile(jsonType, newJson)
  }

  private def matchRightFile(jsonType: String, newJSON: JSONObject) = jsonType match {
    case "poll" =>
      val pollsString = Source.fromFile(pathToPolls).getLines.mkString
      val pollsJson = new JSONObject(pollsString)
      val polls: JSONArray = pollsJson.getJSONArray("data")
      polls.put(newJSON)
      val newPolls = createNewStr(polls)
      finalSave(pathToPolls, newPolls)

    case "pollanswer" =>
      val pollAnswersString = Source.fromFile(pathToPollAnswers).getLines.mkString
      val pollAnswersJson = new JSONObject(pollAnswersString)
      val pollAnswers: JSONArray = pollAnswersJson.getJSONArray("data")
      pollAnswers.put(newJSON)
      val newPollAnswers = createNewStr(pollAnswers)
      finalSave(pathToPollAnswers, newPollAnswers)

    case "comment" =>
      val commentsString = Source.fromFile(pathToComments).getLines.mkString
      val commentsJson = new JSONObject(commentsString)
      val comments: JSONArray = commentsJson.getJSONArray("data")
      comments.put(newJSON)
      val newComments = createNewStr(comments)
      finalSave(pathToComments, newComments)

    case "statement"=>
      val statementsString = Source.fromFile(pathToStatements).getLines.mkString
      val statementsJson = new JSONObject(statementsString)
      val statements: JSONArray = statementsJson.getJSONArray("data")
      statements.put(newJSON)
      val newStatements = createNewStr(statements)
      finalSave(pathToStatements, newStatements)
  }

  private def createNewStr(_jsonArray: JSONArray): String = {
    "{\"data\": " + _jsonArray.toString() + "}"
  }

  private def finalSave(jsonFile: String, jString: String): Unit = {
    val parsedString = gsonParser.parse(jString)
    val gString = gsonFormatter.toJson(parsedString)
    val bufferedJsonWriter = new BufferedWriter(new FileWriter(jsonFile))
    bufferedJsonWriter.write(gString)
    bufferedJsonWriter.close()
  }
}

