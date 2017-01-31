package main.client.view

import main.client.model.Statement

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.collections.ObservableMap
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, TextField, TextInputDialog}
import scalafx.scene.layout.{BorderPane, HBox, VBox}
import scalafx.scene.text.{Text, TextFlow}

object ClientView extends JFXApp {




    //Variablen sind provisorisch, die Sachen müssen eigentlich vom Server geholt werden, und neue Kommentare auch dort hin geschickt werden
    val statement1 = new Statement(1, "statement 1 statement 1 statement 1 statement 1 statement 1 statement 1 statement 1")
    val statement2 = new Statement(2, "statement 2")

    val statements = List(statement1, statement2)

    var likes = ObservableMap(1 -> 3, 2 -> 4)

    var comments = ObservableMap(1 -> List("comment 1 for statement 1", "comment 2 for statement 1"), 2 -> List("comment 1 for statement 2", "comment 2 for statement 2"))

    var polls = ObservableMap(1 -> List(), 2 -> List())

  def getStage():PrimaryStage= {

    stage = new PrimaryStage {
      title = "Channel Poll"
      height = 600
      width = 500
      scene = new Scene {
        val border = new BorderPane()
        border.top = new Label("ChannelPoll")
        border.bottom = new Button("Logout")
        //border.left
        //border.right


        //VerticalBox, die später alle StatementBoxen enthalten soll
        val vbox = new VBox()


        //Für jedes Statement wird eine StatementBox erstellt
        //var statementBoxes = statements.map(s =>
        //  createStatementBox(s)
        //)

        //Die erzeugten StatementBoxen werden der VerticalBox hinzugefügt
        //statementBoxes.foreach(box => vbox.children.add(box))

        border.center = vbox
        root = border
      }

    }
    stage
  }

  //Funktion, die für ein gegebenes Statement eine StatementBox erzeugt
  def createStatementBox(statement: Statement): VBox = {
    //StatementText
    val statementTextFlow = new TextFlow(new Text(statement.text))

    //InfoBox
    val numberOfLikes = new Text(likes(statement.id).toString + " Likes")
    val numberOfComments = new Text(comments(statement.id).length.toString + " Comments")
    val numberOfPolls = new Text(polls(statement.id).length.toString + " Polls")
    val infoBox = new HBox(numberOfLikes, new Text("\t"), numberOfComments, new Text("\t"), numberOfPolls)

    //Like Button
    val likeButton = new Button("Like")
    likeButton.onAction = e => {
      println("Like button clicked")
      val countLikes = likes(statement.id) + 1
      likes += (statement.id -> countLikes)
    }

    //Comment Button
    val commentButton = new Button("Comment")
    commentButton.onAction = e => {
      println("Comment button clicked")
    }

    //Poll Button
    val pollButton = new Button("Poll")
    pollButton.onAction = e => {
      println("Poll button clicked")

      val pollView = new PollView
      pollView.start()
      //pollView öffnen

    }


    val pollList = getPollsFromServer(statement.id)
    var existingPolls = new VBox()


    pollList


    val commentList = getCommentsFromServer(statement.id)
    var existingComments = new TextFlow()

    commentList.foreach(comment => {
      existingComments.children.add(new Text("\n"))
      existingComments.children.add(new Text(comment))
    })

    comments.onChange({
      val updatedCommentList = getCommentsFromServer(statement.id)
      existingComments.children.clear()
      updatedCommentList.foreach(comment => {
        existingComments.children.add(new Text("\n"))
        existingComments.children.add(new Text(comment))
      })
      println("on change activated")
    })

    val newComment = new TextField()
    newComment.promptText = "Write a comment..."
    newComment.onKeyPressed = e => {
      if (e.getCode.getName.equals("Enter")) {
        val comment = newComment.getText()
        sendCommentToServer(statement.id, comment)
        newComment.clear()
      }
    }


    val commentBox = new VBox(newComment, existingComments)


    val pollBox = new VBox()
    return new VBox(statementTextFlow, infoBox, new HBox(likeButton, commentButton, pollButton), commentBox)

  }


  def getCommentsFromServer(statementId: Int): List[String] = {
    //TODO: comments vom server holen
    return comments(statementId)
  }

  def getPollsFromServer(statementId: Int): List[Poll] = {
    //TODO
    return polls(statementId)
  }


  def sendCommentToServer(statementId: Int, comment: String): Unit = {
    val existingCommentList = comments(statementId)
    println("Existing Comment List: " + existingCommentList)
    val updatedCommentList = comment :: existingCommentList
    println("New Comment List: " + updatedCommentList)
    comments += (statementId -> updatedCommentList)
    println("Comments: " + comments)
  }

}
