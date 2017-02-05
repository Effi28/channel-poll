package main.client.view



import java.util.Calendar

import main.client.controller.Controller
import main.server.serverCommunication.ClientControl
import main.shared.{Message, Poll, Statement}

import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, HBox, VBox}
import scalafx.scene.text.{Text, TextFlow}
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer
import scalafx.scene.control.ScrollPane.ScrollBarPolicy


object ClientView extends JFXApp {


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


  def generalTabContent(): BorderPane = {
    val border = new BorderPane()


    //Top
    border.top = new Label("Political Statements")


    //Left
    //border.left


    //Center: Content





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


    //Right: User
    val userList = new ListView[String]
    userList.items = ClientControl.users
    ClientControl.users.onChange({
      userList.items = ClientControl.users
    })
    border.right = userList


    //Bottom: Logout
    border.bottom = logoutButton()


    return border
  }


  def statementTabContent(statement: Statement): BorderPane = {
    val border = new BorderPane()


    //Top
    border.top = new Label("Chat")


    //Left


    //Center: Content


    val user = new Text(statement.userName)
    val message = new TextFlow(new Text(statement.message))


    val chatFeed = new VBox()
    val scrollPane = new ScrollPane()
    scrollPane.content = chatFeed
    scrollPane.hbarPolicy = ScrollBarPolicy.Never


    /*
        ClientControl.chatMessages.get(statement.ID).get.onChange({
          chatFeed.children.add(new Text(ClientControl.chatMessages.get(statement.ID).get.last.msg)):Unit
        })
        */


    val messageInputField = new TextArea()

    val sendButton = new Button("Send")
    sendButton.autosize()
    sendButton.onAction = e => {
      val messageText = messageInputField.getText
      if (messageText.size != 0) {
        val createdAt = Calendar.getInstance().getTime.toString
        //TODO: receiver => ?
        val sender = null
        val receiver = null
        val message = new Message(sender, createdAt, messageText, receiver, statement.ID)
        ClientControl.sendMessage(message)
        messageInputField.clear()
      }
    }


    val pollTemplate = new VBox()


    val pollButton = new Button("Poll")
    pollButton.onAction = e => {

      val questionLabel = new Label("Question")
      val questionInputField = new TextField()
      questionInputField.requestFocus()
      val questionTemplate = new HBox(questionLabel, questionInputField)

      val optionLabel1 = new Label("Option 1")
      val optionInputField1 = new TextField()
      val optionTemplate1 = new HBox(optionLabel1, optionInputField1)

      val optionLabel2 = new Label("Option 2")
      val optionInputField2 = new TextField()
      val optionTemplate2 = new HBox(optionLabel2, optionInputField2)

      val optionHashMap = new HashMap[Int, (Label, TextField)]
      optionHashMap.put(1, (optionLabel1, optionInputField1))
      optionHashMap.put(2, (optionLabel2, optionInputField2))

      var countOptions = 2

      val optionBox =  new VBox(optionTemplate1, optionTemplate2)

      val addOptionButton = new Button("Add Option")
      addOptionButton.onAction = e => {
        countOptions += 1
        val optionLabel = new Label("Option " + countOptions)
        val optionInputField = new TextField()
        optionHashMap.put(countOptions, (optionLabel, optionInputField))
        val optionTemplate = new HBox(optionLabel, optionInputField)
        optionBox.children.add(optionTemplate)
      }

      val submitButton = new Button("Submit")
      submitButton.onAction = e => {

        val createdAt = Calendar.getInstance().getTime.toString

        //TODO: eigenen user setzen
        val user = null


        val question = questionInputField.getText
        val options = new HashMap[Int, (String, Int)]()

        optionHashMap.foreach(x => {
          val key = x._1
          val value = x._2
          val optionInputField= value._2

          options.put(key, (optionInputField.getText, 0))

        })
        val pollid = 1 // todo ids generieren
        val poll = new Poll(pollid, statement.ID, createdAt, user, question, options)

        ClientControl.sendPoll(poll)

        pollTemplate.children.clear()
        //TODO: submit poll
      }

      pollTemplate.children.addAll(questionTemplate, optionBox, addOptionButton, submitButton)
    }

    border.center = new VBox(new VBox(user, message), scrollPane, new HBox(messageInputField, new VBox(sendButton, pollButton)), pollTemplate)


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


  def enterChatRoomButton(statement: Statement): Button = {
    val enterChatRoomButton = new Button("Enter Chat Room")
    enterChatRoomButton.onAction = e => {

      Controller.getChatRooms().add(statement)
    }
    return enterChatRoomButton
  }


  def logoutButton(): Button = {
    val logoutButton = new Button("Logout")
    logoutButton.onAction = e => {
      logout()
    }
    return logoutButton
  }


  def logout(): Unit = {
    Controller.logout()
  }
}
