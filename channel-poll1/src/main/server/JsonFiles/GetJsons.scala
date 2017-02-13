package main.server.JsonFiles

import org.json.{JSONArray, JSONObject}

import scala.io.Source

/**
  * Created by KathrinNetzer on 10.02.2017.
  */
final object GetJsons {
  private val pathToStatements = "src/main/server/JsonFiles/Statement.json"
  private val pathToComments = "src/main/server/JsonFiles/Comments.json"
  private val pathToPolls = "src/main/server/JsonFiles/Poll.json"
  private val pathToPollAnswers = "src/main/server/JsonFiles/PollAnswer.json"

  def getLastStatements(): JSONArray = {
    SaveJsons.writeStatementsFlag = true
    val statementsString = Source.fromFile(pathToStatements).getLines.mkString
    SaveJsons.writeStatementsFlag = false
    val statementsJson = new JSONObject(statementsString)
    val statements: JSONArray = statementsJson.getJSONArray("data")
    val lastStatements: JSONArray = new JSONArray()
    if (statements.length() > 10) {
      for (i <- (0 until 10).reverse) {
        lastStatements.put(statements.getJSONObject(statements.length() - i - 1))
      }
      return lastStatements
    } else {
      return statements
    }
  }

  def getLastCPP(dataType: String): JSONArray = {
    val strFromJson = reloadFile(dataType)
    val newJson = new JSONObject(strFromJson)
    val reloadedArray: JSONArray = newJson.getJSONArray("data")
    return reloadedArray
  }

  def reloadFile(dataType: String): String = dataType match {
    case "Poll" =>
      SaveJsons.writePollsFlag = true
      val pollsString = Source.fromFile(pathToPolls).getLines.mkString
      SaveJsons.writePollsFlag = false
      return pollsString
    case "PollAnswer" =>
      SaveJsons.writePollAnswersFlag = true
      val pollAnswersString = Source.fromFile(pathToPollAnswers).getLines.mkString
      SaveJsons.writePollAnswersFlag = false
      return pollAnswersString
    case "Comment" =>
      SaveJsons.writeCommentsFlag = true
      val commentsString = Source.fromFile(pathToComments).getLines.mkString
      SaveJsons.writeCommentsFlag = false
      return commentsString
  }
}
