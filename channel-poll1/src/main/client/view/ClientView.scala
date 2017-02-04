package main.client.view


import main.shared.{Comment, Message, Statement}

import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.collections.{ObservableBuffer, ObservableHashSet, ObservableMap}
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, HBox, VBox}
import scalafx.scene.text.{Text, TextFlow}
import main.server.serverCommunication.ClientControl

import scalafx.scene.control.ScrollPane.ScrollBarPolicy


object ClientView extends JFXApp {


  def getStage(): PrimaryStage = {


    stage = new PrimaryStage {
      title = "Channel Poll"
      height = 900
      width = 700
      scene = new Scene {
        val border = new BorderPane()


        //Top
        border.top = new Label("ChannelPoll")


        //Left
        //border.left


        //Center: Content


        //Right: User
        val userList = new ListView[String]
        userList.items = ClientControl.users
        ClientControl.users.onChange({
          userList.items = ClientControl.users
        })
        border.right = userList


        val statementList = new ListView[Statement]()


        ClientControl.statements.onChange({
          statementList.items = ClientControl.statements
          val activity = createActivity(ClientControl.statements.last)
          addActivity(activity)
        })

        val feed = new VBox()
        feed.children = ClientControl.activityFeed
        val scroll = new ScrollPane()
        scroll.content = feed
        scroll.hbarPolicy = ScrollBarPolicy.Never
        border.center = scroll

        ClientControl.activityFeed.onChange({
          Platform.runLater {
            feed.children.add(ClientControl.activityFeed.last): Unit
          }
        })


        //VerticalBox, die später alle StatementBoxen enthalten soll
        //val vbox = new VBox()


        //Für jedes Statement wird eine StatementBox erstellt
        //var statementBoxes = statements.map(s =>
        //  createStatementBox(s)
        //)

        //Die erzeugten StatementBoxen werden der VerticalBox hinzugefügt
        //statementBoxes.foreach(box => vbox.children.add(box))


        //border.center = messageList


        //Bottom: Logout
        val logoutButton = new Button("Logout")
        logoutButton.onAction = e => {
          logout()
        }
        border.bottom = logoutButton


        //Root
        root = border
      }

    }
    stage
  }

  /*
    def getStage(): PrimaryStage ={
      stage
    }
    */


  def createActivity(statement: Statement): VBox = {
    val user = new Text(statement.userName)
    val message = new TextFlow(new Text(statement.message))
    return new VBox(user, message, infoBox(), actionBox())
  }

  def infoBox(): HBox = {
    //InfoBox
    val numberOfLikes = 0
    //TODO
    val likes = new Text(numberOfLikes + " Likes")
    val numberOfComments = 0
    //TODO
    val comments = new Text(numberOfComments + " Comments")
    val numberOfPolls = 0
    //TODO
    val polls = new Text(numberOfPolls + " Polls")
    return new HBox(likes, new Text("\t"), comments, new Text("\t"), polls)
  }

  def actionBox(): HBox = {
    return new HBox(likeButton(), commentButton(), pollButton())
  }

  def likeButton(): Button = {
    val likeButton = new Button("Like")
    likeButton.onAction = e => {
      //TODO
    }
    return likeButton
  }

  def commentButton(): Button = {
    val commentButton = new Button("Comment")
    commentButton.onAction = e => {
      //TODO
    }
    return commentButton
  }

  def pollButton(): Button = {
    val pollButton = new Button("Poll")
    pollButton.onAction = e => {
      //TODO
    }
    return pollButton
  }

  def addActivity(activity: VBox): Unit = {
    ClientControl.activityFeed += activity
  }



/*
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

  def logout(): Unit = {
    //TODO: TwitterLogout
    //Fenster schließen
  }

}
