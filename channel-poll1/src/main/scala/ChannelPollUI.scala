
import scalafx.scene.control._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.collections.ObservableMap
import scalafx.scene.Scene
import scalafx.scene.layout._
import scalafx.scene.paint.Color._
import scalafx.scene.text.{Text, TextFlow}


object ChannelPollUI extends JFXApp {

  //Variablen sind provisorisch, die Sachen müssen eigentlich vom Server geholt werden, und neue Kommentare auch dort hin geschickt werden
  val statement1 = new Statement(1, "statement 1 statement 1 statement 1 statement 1 statement 1 statement 1 statement 1")
  val statement2 = new Statement(2, "statement 2")

  val statements = List(statement1, statement2)


  var comments = ObservableMap(1 -> List("comment 1 for statement 1", "comment 2 for statement 1"), 2 -> List("comment 1 for statement 2", "comment 2 for statement 2"))

  var polls = ObservableMap(1 -> List(), 2 -> List())

  override val stage = new PrimaryStage {
    title = "Channel Poll"
    height = 600
    width = 500
    scene = new Scene {
      val border = new BorderPane()


      var statementBoxes = statements.map(s =>
        createStatementBox(s)
      )


      border.top = new Label("ChannelPoll")
      border.bottom = new Button("Logout")
      //border.left
      //border.right
      val vbox = new VBox()


      statementBoxes.foreach(box => vbox.children.add(box))

      vbox

      border.center = vbox

      root = border


    }


    def createStatementBox(statement: Statement): VBox = {

      val statementTextFlow = new TextFlow(new Text(statement.text))


      //Like Button
      val likeButton = new Button("Like")
      likeButton.onAction = e => {
        println("Like button clicked")
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

        val dialog = new TextInputDialog() {
          initOwner(stage)
          title = "Create Poll Dialog"
          headerText = "You're about to start a new poll"
          contentText = "Ask something..."
        }

        val result = dialog.showAndWait()

        result match {
          case Some(question) => {
            println("Your question: " + question)
            //TODO: id muss angepasst werden
            val newPoll = new Poll(0,statement.id, question)

          }


          case None => println("Dialog was canceled.")
        }
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
      return new VBox(statementTextFlow, new HBox(likeButton, commentButton, pollButton), commentBox)

    }




    def getCommentsFromServer(statementId: Int): List[String] = {
      //TODO: comments vom server holen
      return comments(statementId)
    }


    def getPollsFromServer(statementId: Int): List[Poll] ={
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
}