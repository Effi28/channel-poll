package main.client.view


import java.util.Calendar

import main.shared.{Message, Poll, Statement}

import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, HBox, VBox}
import scalafx.scene.text.{Text, TextFlow}
import main.server.serverCommunication.ClientControl

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
        ClientControl.chatRooms.onChange({
          val statementTab = new Tab()
          statementTab.text = ClientControl.chatRooms.last.userName
          statementTab.content = statementTabContent(ClientControl.chatRooms.last)
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


    ClientControl.statements.onChange({
      statementList.items = ClientControl.statements
      val activity = createActivity(ClientControl.statements.last)
      addActivity(activity)
    })

    val feed = new VBox()
    feed.children = ClientControl.activityFeed
    val scrollPane = new ScrollPane()
    scrollPane.content = feed
    scrollPane.hbarPolicy = ScrollBarPolicy.Never
    border.center = scrollPane

    ClientControl.activityFeed.onChange({
      Platform.runLater {
        feed.children.add(ClientControl.activityFeed.last): Unit
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



        val poll = new Poll(statement.ID, createdAt, user, question, options)

        ClientControl.sendPoll(poll)

        pollTemplate.children.clear()
        //TODO: submit poll
      }

      pollTemplate.children.addAll(questionTemplate, optionBox, addOptionButton, submitButton)
    }

    border.center = new VBox(new VBox(user, message), scrollPane, new HBox(messageInputField, new VBox(sendButton, pollButton)), pollTemplate)


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


  def createActivity(statement: Statement): VBox = {
    val user = new Text(statement.userName)
    val message = new TextFlow(new Text(statement.message))
    return new VBox(user, message, enterChatRoomButton(statement))
  }

  def addActivity(activity: VBox): Unit = {
    ClientControl.activityFeed += activity
  }


  def enterChatRoomButton(statement: Statement): Button = {
    val enterChatRoomButton = new Button("Enter Chat Room")
    enterChatRoomButton.onAction = e => {
      ClientControl.chatRooms.add(statement)
      if (!ClientControl.chatMessages.contains(statement.ID)) {
        ClientControl.chatMessages.put(statement.ID, new ObservableBuffer[Message]())
      }
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
    //TODO: TwitterLogout
    //Fenster schließen
  }

}
