package main.client.view


import main.client.controller.Controller
import main.shared.{Statement}
import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, HBox, VBox}
import scalafx.scene.text.{Text, TextFlow}

import scala.collection.mutable.ListBuffer
import scalafx.scene.control.ScrollPane.ScrollBarPolicy


object ClientView extends JFXApp {


  //val statementTabs = new ObservableBuffer[Statement]()


  def getStage(): PrimaryStage = {
    stage = new PrimaryStage {
      title = "Channel Poll"
      height = 900
      width = 700
      scene = new Scene {

        val tabPane = new TabPane()
        val generalTab = new Tab()
        generalTab.text = "General"
        generalTab.content = generalTabContent()

        val tabList = ListBuffer(generalTab)
        tabPane.tabs = tabList
        Controller.getChatRooms().onChange({
              val statementTab = new Tab()
              statementTab.text = Controller.getChatRooms().last.userName
              statementTab.content = statementTabContent(Controller.getChatRooms().last)
              tabList += statementTab
              tabPane.tabs = tabList
            })










        //Root
        root = tabPane
      }

    }
    stage
  }

  /*
    def getStage(): PrimaryStage ={
      stage
    }
    */


  def generalTabContent(): BorderPane = {
    val border = new BorderPane()


    //Top
    border.top = new Label("Political Statements")


    //Left
    //border.left


    //Center: Content


    //Right: User
    val userList = new ListView[String]
    userList.items = Controller.getUsers()
    Controller.getUsers().onChange({
      userList.items = Controller.getUsers()
    })
    border.right = userList


    val statementList = new ListView[Statement]()


    Controller.getStatements().onChange({
      statementList.items = Controller.getStatements()
      val activity = createActivity(Controller.getStatements().last)
      addActivity(activity)
    })

    val feed = new VBox()
    feed.children = Controller.getActivityFeedback
    val scroll = new ScrollPane()
    scroll.content = feed
    scroll.hbarPolicy = ScrollBarPolicy.Never
    border.center = scroll

    Controller.getActivityFeedback.onChange({
      Platform.runLater {
        feed.children.add(Controller.getActivityFeedback.last): Unit
      }
    })


    //Bottom: Logout
    border.bottom = logoutButton()


    return border
  }


  def statementTabContent(statement: Statement):BorderPane={
    val border = new BorderPane()




    val user = new Text(statement.userName)
    val message = new TextFlow(new Text(statement.message))


    //Top
    border.top = new VBox(user, message)




    //Left
    //border.left


    //Center: Content


    //Right: User
    val userList = new ListView[String]
    userList.items = Controller.getUsers()
    Controller.getUsers().onChange({
      userList.items = Controller.getUsers()
    })
    border.right = userList



    //Bottom: Logout
    border.bottom = logoutButton()






    return border
  }


  def createActivity(statement: Statement): VBox = {
    val user = new Text(statement.userName)
    val message = new TextFlow(new Text(statement.message))
    return new VBox(user, message, enterChatRoomButton(statement))
  }

  def addActivity(activity: VBox): Unit = {
    Controller.getActivityFeedback += activity
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

  def enterChatRoomButton(statement: Statement): Button = {
    val enterChatRoomButton = new Button("Enter Chat Room")
    enterChatRoomButton.onAction = e => {
      Controller.getChatRooms().add(statement)
    }
    return enterChatRoomButton
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

  def logoutButton(): Button ={
    val logoutButton = new Button("Logout")
    logoutButton.onAction = e=> {
      logout()
    }
    return logoutButton
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
    Controller.logout()
  }
}
