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

  /*
  def getLastStatements(): JSONArray = {
    SaveJsons.writeStatementsFlag = true
    wait(2)
    val statementsString = Source.fromFile(pathToStatements).getLines.mkString
    SaveJsons.writeCommentsFlag = false

    val statementsJson = new JSONObject(statementsString)
    val statements: JSONArray = statementsJson.getJSONArray("data")


    val lastStatements: JSONArray = JSONArray
    if (statements.length() >= 10) {
      for (i <- -10 until -1) {
        lastStatements.put(statements.getJSONObject(i))
      }
      return lastStatements
    } else {
      return statements
    }

  }
  */
}
