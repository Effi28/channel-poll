package main.client.view


import java.util.Calendar

import main.client.controller.Controller
import main.shared._
import main.shared.data._

import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.{BorderPane, GridPane, VBox}
import scalafx.scene.text.{Text, TextFlow}
import scala.collection.mutable.{ArrayBuffer, HashMap, ListBuffer}
import scalafx.beans.property.IntegerProperty
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control._


final object ClientView extends JFXApp {
  var pollTemplateIsVisible = false

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

          //TODO brenda please move that where it belongs
          statementTab.onClosed = e => {
            Controller.unsubscribe(Controller.getChatRooms().last)
          }
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
    userList.items = Controller.getUsers()
    Controller.getUsers().onChange({
      userList.items = Controller.getUsers()
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


    Controller.getCommentsForStatement(statement).onChange({
      Platform.runLater {
        val latestComment = Controller.getCommentsForStatement(statement).last
        val comment = renderComment(latestComment)

        chatFeed.children.add(comment)
      }


    })


    Controller.getPollsForStatement(statement).onChange({
      Platform.runLater {
        val latestPoll = Controller.getPollsForStatement(statement).last
        val poll = renderActivePoll(latestPoll)
        chatFeed.children.add(poll)
      }
    })


    val chatGridPane = new GridPane()

    val commentInputField = new TextArea()

    val sendButton = new Button("Send")
    sendButton.autosize()
    sendButton.onAction = e => {


      val message = commentInputField.getText


      if (message.size != 0) {
        val createdAt = Calendar.getInstance().getTime.toString


        //TODO: receiver => ?
        //val sender = Controller.getTwitterUser().screenname

        val userID = Controller.getTwitterUser().ID


        val userName = Controller.getTwitterUser().userName


        val id = 0
        //TODO: id setzen
        val comment = new Comment(id, statement.ID, userID, userName, message, createdAt)
        Controller.sendComment(comment)
        commentInputField.clear()
      }
    }


    val pollTemplate = new VBox()

    val pollButton = new Button("Poll")
    pollButton.onAction = e => {
      if (!pollTemplateIsVisible) {
        pollTemplateIsVisible = true
        val questionLabel = new Label("Question")
        val questionInputField = new TextField()
        questionInputField.requestFocus()

        val optionLabel1 = new Label("Option 1")
        val optionInputField1 = new TextField()

        val optionLabel2 = new Label("Option 2")
        val optionInputField2 = new TextField()

        val optionHashMap = new HashMap[Int, (Label, TextField)]
        optionHashMap.put(1, (optionLabel1, optionInputField1))
        optionHashMap.put(2, (optionLabel2, optionInputField2))


        val pollGridPane = new GridPane()
        pollGridPane.addRow(0, questionLabel, questionInputField)
        pollGridPane.addRow(2, optionLabel1, optionInputField1)
        pollGridPane.addRow(3, optionLabel2, optionInputField2)

        var countOptions = 2
        var currentRowIndex = IntegerProperty(3)

        val addOptionButton = new Button("Add Option")
        addOptionButton.onAction = e => {
          countOptions += 1
          currentRowIndex.value = currentRowIndex.value + 1

          val optionLabel = new Label("Option " + countOptions)
          val optionInputField = new TextField()
          optionHashMap.put(countOptions, (optionLabel, optionInputField))

          pollGridPane.addRow(currentRowIndex.value, optionLabel, optionInputField)
        }


        var indexForAddOptionButton = currentRowIndex + 1
        var indexForSubmitButton = currentRowIndex + 2

        currentRowIndex.onChange({
          indexForAddOptionButton = currentRowIndex + 1
          indexForSubmitButton = currentRowIndex + 2

        })


        pollGridPane.addRow(indexForAddOptionButton.intValue(), addOptionButton)

        val submitButton = new Button("Submit")
        submitButton.onAction = e => {

          val createdAt = Calendar.getInstance().getTime.toString

          val user: TwitterUser = Controller.getTwitterUser()


          val question = questionInputField.getText

          if (question.length == 0) {
            //TODO: Fehler anzeigen, dass Feld nicht leer sein darf
          }

          val options = new ArrayBuffer[Option]()

          optionHashMap.foreach(x => {
            val key = x._1
            val value = x._2
            val optionInputField = value._2


            if (optionInputField.getText.length == 0) {
              //TODO: Fehler anzeigen, dass Feld nicht leer sein darf
            }

            options += new Option(key, optionInputField.getText, 0)

          })
          // todo ids generieren
          val pollID = 1

          val poll = new Poll(pollID, statement.ID, user.ID, user.userName, question, options, createdAt)
          Controller.sendPoll(poll)
          pollTemplate.children.clear()
          pollTemplateIsVisible = false
        }

        pollGridPane.addRow(indexForSubmitButton.intValue(), submitButton)

        pollTemplate.children.addAll(pollGridPane)
      }
    }


    commentInputField.setMaxHeight(Double.MaxValue)
    val commentField = new VBox()
    commentField.children.add(commentInputField)
    chatGridPane.add(commentField, 0, 0, 2, 1)


    sendButton.setMaxWidth(Double.MaxValue)
    pollButton.setMaxWidth(Double.MaxValue)
    val buttons = new VBox(sendButton, pollButton)
    chatGridPane.add(buttons, 2, 0, 1, 2)

    border.center = new VBox(new VBox(user, message), scrollPane, chatGridPane, pollTemplate)


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


  def renderComment(comment: Comment): GridPane = {
    val commentGrid = new GridPane()
    commentGrid.addRow(0, new Label(comment.userName), new Label(" : "), new Text(comment.message))
    return commentGrid
  }

  def renderActivePoll(poll: Poll): GridPane = {
    val pollGrid = new GridPane()
    var rowIndex = 0
    val columnIndex = 2
    pollGrid.addRow(rowIndex, new Label(poll.userName), new Label(" : "), new Text(poll.question))


    val toggleGroup = new ToggleGroup()

    poll.options.foreach(option => {
      rowIndex += 1
      val optionId = option.key
      val optionInfo = option.name
      val radioButton = new RadioButton(optionInfo)
      radioButton.id = optionId.toString
      radioButton.onAction = e => {
        radioButton.selected = true
      }
      toggleGroup.toggles.add(radioButton)
      pollGrid.add(radioButton, columnIndex, rowIndex)
    })
    rowIndex += 1
    val submitButton = new Button("Submit Answer")
    submitButton.onAction = e => {
      val selectedButton = toggleGroup.selectedToggle.value.asInstanceOf[javafx.scene.control.RadioButton]



      val selectedButtonId = selectedButton.getId
      val selectedButtonText = selectedButton.getText
      val stamp = Calendar.getInstance().getTime.toString





      val pollAnswer = new PollAnswer(poll.ID, poll.statementID, poll.userID, poll.userName, poll.question, (selectedButtonId.toString.toInt, selectedButtonText.toString),
        stamp)

      Controller.sendPollAnswer(pollAnswer)

    }
    pollGrid.add(submitButton, columnIndex, rowIndex)
    return pollGrid
  }


  def createActivity(statement: Statement): VBox = {
    val activity = new VBox(new Label(statement.userName), new Text(statement.message), enterChatRoomButton(statement))
    return activity
  }

  def addActivity(activity: VBox): Unit = {
    Controller.getActivityFeedback += activity
  }


  def enterChatRoomButton(statement: Statement): Button = {
    val enterChatRoomButton = new Button("Enter Chat Room")
    enterChatRoomButton.onAction = e => {

      Controller.getChatRooms().add(statement)
      Controller.subscribe(statement)

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
