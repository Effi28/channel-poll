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
  private val pathToStatements = "src/main/server/JsonFiles/Statement.json"
  private val pathToComments = "src/main/server/JsonFiles/Comments.json"
  private val pathToPolls = "src/main/server/JsonFiles/Poll.json"
  private val pathToPollAnswers = "src/main/server/JsonFiles/PollAnswer.json"

  private val gsonFormatter = new GsonBuilder().setPrettyPrinting().create
  private val gsonParser = new JsonParser()

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
      if (statementsJsonQueue.nonEmpty) {
        val firstStatement = statementsJsonQueue.dequeue()
        writeStatementsFlag = true
        save_json(firstStatement)
        writeStatementsFlag = false
      }
      if (commentsJsonQueue.nonEmpty) {
        val firstComment = commentsJsonQueue.dequeue()
        writeCommentsFlag = true
        save_json(firstComment)
        writeCommentsFlag = false
      }
      if (pollsAnswersQueue.nonEmpty) {
        val firstPollAnswer = pollsAnswersQueue.dequeue()
        writePollAnswersFlag = true
        save_json(firstPollAnswer)
        writePollAnswersFlag = false
      }
      if (pollsQueue.nonEmpty) {
        val firstPoll = pollsQueue.dequeue()
        writePollsFlag = true
        save_json(firstPoll)
        writePollsFlag = false
      }
      Thread.sleep(7000)
    })

  private def save_json(newJson: JSONObject) : Unit = {
    val jsonType: String = newJson.optString("type")
    matchRightFile(jsonType, newJson)
  }

  private def matchRightFile(jsonType: String, newJSON: JSONObject) = jsonType match {
    case "POLL" =>
      val pollsString = Source.fromFile(pathToPolls).getLines.mkString
      val pollsJson = new JSONObject(pollsString)
      val polls: json.JSONArray = pollsJson.getJSONArray("data")
      polls.put(newJSON)
      val newPolls = createNewStr(polls)
      finalSave(pathToPolls, newPolls)

    case "POLLANSWER" =>
      val pollAnswersString = Source.fromFile(pathToPollAnswers).getLines.mkString
      val pollAnswersJson = new JSONObject(pollAnswersString)
      val pollAnswers: json.JSONArray = pollAnswersJson.getJSONArray("data")
      pollAnswers.put(newJSON)
      val newPollAnswers = createNewStr(pollAnswers)
      finalSave(pathToPollAnswers, newPollAnswers)

    case "COMMENT" =>
      val commentsString = Source.fromFile(pathToComments).getLines.mkString
      val commentsJson = new JSONObject(commentsString)
      val comments: json.JSONArray = commentsJson.getJSONArray("data")
      comments.put(newJSON)
      val newComments = createNewStr(comments)
      finalSave(pathToComments, newComments)

    case "STATEMENT" =>
      val statementsString = Source.fromFile(pathToStatements).getLines.mkString
      val statementsJson = new JSONObject(statementsString)
      val statements: json.JSONArray = statementsJson.getJSONArray("data")
      statements.put(newJSON)
      val newStatements = createNewStr(statements)
      finalSave(pathToStatements, newStatements)
  }

  private def createNewStr(_jsonArray: JSONArray): String = {
    "{\"data\": " + _jsonArray.toString() + "}"
  }

  private def finalSave(jsonFile:String, jString: String): Unit = {
    val parsedString = gsonParser.parse(jString)
    val gString = gsonFormatter.toJson(parsedString)
    val bufferedJsonWriter = new BufferedWriter(new FileWriter(jsonFile))
    bufferedJsonWriter.write(gString)
    bufferedJsonWriter.close()
  }
}

