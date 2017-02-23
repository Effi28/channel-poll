package main.server.JsonFiles

import java.io._

import com.google.gson._
import main.shared.enums.JsonType
import org.json.{JSONArray, JSONObject}
import main.shared.enums.JsonType
import scala.collection.mutable.Queue
import scala.io.Source

/**
  * Created by KathrinNetzer on 29.01.2017.
  */
final object SaveJsons {

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
  var statementsJsonQueue = new Queue[JSONObject]
  var commentsJsonQueue = new Queue[JSONObject]
  var pollsAnswersQueue = new Queue[JSONObject]
  var pollsQueue = new Queue[JSONObject]
  var writeStatementsFlag = false
  var writeCommentsFlag = false
  var writePollAnswersFlag = false
  var writePollsFlag = false

  private def save_json(newJson: JSONObject): Unit = {
    if(newJson.length() != 0){
      val jsonType: JsonType.JsonType = JsonType.withName(newJson.optString("type"))
      matchRightFile(jsonType, newJson)
    }
  }

  private def matchRightFile(jsonType: JsonType.JsonType, newJSON: JSONObject) = jsonType match {
    case JsonType.POLL =>
      val pollsString = Source.fromFile(pathToPolls).getLines.mkString
      val pollsJson = new JSONObject(pollsString)
      var polls: JSONArray = pollsJson.getJSONArray("data")
      polls.put(newJSON)
      polls = checkCommPollPollAnw(polls)
      val newPolls = createNewStr(polls)
      finalSave(pathToPolls, newPolls)

    case JsonType.POLLANSWER=>
      val pollAnswersString = Source.fromFile(pathToPollAnswers).getLines.mkString
      val pollAnswersJson = new JSONObject(pollAnswersString)
      var pollAnswers: JSONArray = pollAnswersJson.getJSONArray("data")
      pollAnswers.put(newJSON)
      pollAnswers = checkCommPollPollAnw(pollAnswers)
      val newPollAnswers = createNewStr(pollAnswers)
      finalSave(pathToPollAnswers, newPollAnswers)

    case JsonType.COMMENT =>
      val commentsString = Source.fromFile(pathToComments).getLines.mkString
      val commentsJson = new JSONObject(commentsString)
      var comments: JSONArray = commentsJson.getJSONArray("data")
      comments.put(newJSON)
      comments = checkCommPollPollAnw(comments)
      val newComments = createNewStr(comments)
      finalSave(pathToComments, newComments)

    case JsonType.STATEMENT =>
      var statements = loadStatements()
      statements.put(newJSON)
      // don't save more than 20 statemets
      statements = checkLength(statements, 20)
      val newStatements = createNewStr(statements)
      finalSave(pathToStatements, newStatements)

    case _ => print ("NOTHING MATCHED" + newJSON.toString())//TODO
  }

  private def checkLength(array: JSONArray, maxlength: Int): JSONArray = {
    val shortenedArray: JSONArray = new JSONArray()
    val l = array.length()
    if (l >= maxlength) {

      for (i <- (0 until maxlength - 1).reverse) {
        shortenedArray.put(array.getJSONObject(l - i - 1))
      }
      return shortenedArray
    }
    else {
      return array
    }
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

  private def checkCommPollPollAnw(jSONArray: JSONArray): JSONArray = {
    val statements = loadStatements()
    val shortenedArray: JSONArray = new JSONArray()
    for (i <- 0 until jSONArray.length()) {
      for (j <- 0 until statements.length()) {
        val statement: JSONObject = statements.getJSONObject(j)
        val statement_id = statement.optLong("id")
        val jsonObj: JSONObject = jSONArray.getJSONObject(i)

        val ref_statement_id: Long = jsonObj.optLong("statementid")
        if (ref_statement_id == statement_id) {
          shortenedArray.put(jsonObj)
        }
      }
    }
    return shortenedArray
  }

  private def loadStatements(): JSONArray = {
    val statementsString = Source.fromFile(pathToStatements).getLines.mkString
    val statementsJson = new JSONObject(statementsString)
    val statements: JSONArray = statementsJson.getJSONArray("data")
    return statements
  }
}