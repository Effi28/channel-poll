package main.client.view


import main.shared.{Comment, Message, Statement}

import scala.compat.Platform
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.collections.{ObservableBuffer, ObservableHashSet, ObservableMap}
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, HBox, VBox}
import scalafx.scene.text.{Text, TextFlow}
import main.server.serverCommunication.ClientControl
import main.server.serverCommunication.ClientControl.{feed, _}


object ClientView extends JFXApp {




  def getStage():PrimaryStage= {




    stage = new PrimaryStage {
      title = "Channel Poll"
      height = 900
      width = 700
      scene = new Scene {
        val border = new BorderPane()
        border.top = new Label("ChannelPoll")

        val logoutButton = new Button("Logout")
        logoutButton.onAction = e =>{
          logout()
        }

        border.bottom = logoutButton
        //border.left


        val userList = new ListView[String]

        userList.items = ClientControl.users
        println("user size beginning: " + ClientControl.users.size)


        ClientControl.users.onChange({

          userList.items = ClientControl.users
          println("user size: " + ClientControl.users.size)

        })




        border.right = userList



      val statementList = new ListView[Statement]()



       val observableMessageList = new ObservableBuffer[String]()

        val messageList = new ListView[HBox]
messageList.items = ClientControl.feed











        ClientControl.statements.onChange({
          println("size: " + ClientControl.statements.size)
          statementList.items = ClientControl.statements



          val messageBox = createHBox(statements.last)
          addMessage(messageBox)

        })


        val feed = new VBox()

        border.center = this.feed
/*
        ClientControl.feed.onChange({
          println("feed size: " +feed.children.size())

          println("something changed")

          this.feed.children.add(ClientControl.feed.last) : Unit

          border.center = this.feed

        })

*/


        //VerticalBox, die später alle StatementBoxen enthalten soll
        //val vbox = new VBox()


        //Für jedes Statement wird eine StatementBox erstellt
        //var statementBoxes = statements.map(s =>
        //  createStatementBox(s)
        //)

        //Die erzeugten StatementBoxen werden der VerticalBox hinzugefügt
        //statementBoxes.foreach(box => vbox.children.add(box))





        //border.center = messageList
        root = border
      }

    }
    stage
  }



  def createHBox(statement: Statement):HBox = {
    val user = new Text(statement.userName)
    val message = new Text(statement.message)

    return new HBox(user, message)
  }

def addMessage(hBox: HBox):Unit={
  ClientControl.feed += hBox

}

  def getMessageFromStatement(statement: Statement){
    return statement.message
  }



  /*
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

    // muss das nicht im Server handler handle Comment passieren ?
    comments += (statementId -> updatedCommentList)
    println("Comments: " + comments)

    // sending comment to server
    val newcomment = new Comment(comment, "", new Array[String](0), statementId)

    // screenname ? likes ?
    ClientControl.sendComment(newcomment)
  }


*/
  def logout(): Unit ={
    //TODO: TwitterLogout
    //Fenster schließen
  }

}
