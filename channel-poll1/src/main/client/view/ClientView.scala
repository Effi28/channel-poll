package main.client.view


import java.util.Calendar
import main.client.controller.Controller
import main.shared.data._
import scalafx.application.{JFXApp, Platform}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.{BorderPane, GridPane, VBox}
import scalafx.scene.text.{Text, TextFlow}
import scala.collection.mutable.{ArrayBuffer, HashMap, ListBuffer}
import scalafx.beans.property.IntegerProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control._


final object ClientView extends JFXApp {
  var pollTemplateIsVisible = false
  var activityFeed: ObservableBuffer[VBox] = null

  def getStage(): PrimaryStage = {
    stage = new PrimaryStage {
      title = "Channel Poll"
      height = 900
      width = 700
      scene = new Scene {
        activityFeed = new ObservableBuffer[VBox]()
        val tabPane = new TabPane()
        val generalTab = new Tab()
        generalTab.text = "General"
        generalTab.content = generalTabContent()

        val tabList = ListBuffer(generalTab)

        val twitterUser = Controller.getTwitterUser
        if (Controller.getUserChatRooms(twitterUser).size > 0){
          Controller.getUserChatRooms(twitterUser).foreach(statement => {
            val statementTab = new Tab()
            statementTab.text = statement.userName
            statementTab.content = statementTabContent(statement)
            statementTab.onClosed = e => {
              Controller.subscribe(statement, false)
            }
            tabList += statementTab
          })
        }

        tabPane.tabs = tabList

        Controller.getUserChatRooms(twitterUser).onChange({
          val statementTab = new Tab()
          statementTab.text = Controller.getUserChatRooms(twitterUser).last.userName
          statementTab.content = statementTabContent(Controller.getUserChatRooms(twitterUser).last)
          statementTab.onClosed = e => {
            Controller.subscribe(Controller.getUserChatRooms(twitterUser).last, false)
          }
          tabList += statementTab
          tabPane.tabs = tabList
        })

        /*
        Controller.getChatRooms().onChange({
          val statementTab = new Tab()
          statementTab.text = Controller.getChatRooms().last.userName
          statementTab.content = statementTabContent(Controller.getChatRooms().last)
          //TODO brenda please move that where it belongs
          statementTab.onClosed = e => {
            Controller.subscribe(Controller.getChatRooms().last, false)
          }
          tabList += statementTab
          tabPane.tabs = tabList
        })
        */


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

    val feed = new VBox()


    if (Controller.getStatements.size > 0) {
      Controller.getStatements.foreach(st => {
        val activity = createActivity(st)
        addActivity(activity)
      })
    }

    if (activityFeed.size > 0) {
      activityFeed.foreach(item => {
        feed.children.add(0, item)
      })

    }


    Controller.getStatements.onChange({
      val activity = createActivity(Controller.getStatements.last)
      addActivity(activity)
    })


    val scroll = new ScrollPane()
    scroll.content = feed
    scroll.hbarPolicy = ScrollBarPolicy.Never
    border.center = scroll

    activityFeed.onChange({
      Platform.runLater {
        feed.children.add(0, activityFeed.last)
      }
    })


    //Right: User
    val userList = new ListView[TwitterUser]
    userList.items = Controller.getUsers
    Controller.getUsers.onChange({
      userList.items = Controller.getUsers
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


    if (Controller.getCommentsForStatement(statement).size > 0) {
      Controller.getCommentsForStatement(statement).foreach(comment => {
        chatFeed.children.add(renderComment(comment))
      })
    }


    Controller.getCommentsForStatement(statement).onChange({
      Platform.runLater {
        val latestComment = Controller.getCommentsForStatement(statement).last
        val comment = renderComment(latestComment)

        chatFeed.children.add(comment)
      }
    })


    if (Controller.getPollsForStatement(statement).size > 0) {
      Controller.getPollsForStatement(statement).foreach(poll => {
        chatFeed.children.add(renderActivePoll(poll))
      })
    }

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

        val userID = Controller.getTwitterUser.ID
        val userName = Controller.getTwitterUser.userName


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

        var pollIsValid = true

        val questionLabel = new Label("Question")
        val questionInputField = new TextField()
        questionInputField.requestFocus()

        val minNumberOfOptions = 2
        val maxNumberOfOptions = 5

        val currentNumberOfOptions = IntegerProperty(0)

        val errorMessageBox = new TextFlow()


        val optionHashMap = new HashMap[Int, (Label, TextField)]

        val pollGridPane = new GridPane()
        pollGridPane.addRow(0, new Label("Poll"))
        pollGridPane.addRow(1, questionLabel, questionInputField)

        var nextFreeIndex = 2

        for (i <- 1 to minNumberOfOptions) {
          val label = new Label("Option " + i)
          val inputField = new TextField()
          optionHashMap.put(i, (label, inputField))
          pollGridPane.addRow(nextFreeIndex, label, inputField)
          currentNumberOfOptions.value = currentNumberOfOptions.value + 1
          nextFreeIndex += 1
        }


        val addOptionButton = new Button("Add Option")
        addOptionButton.onAction = e => {
          currentNumberOfOptions.value = currentNumberOfOptions.value + 1
          val label = new Label("Option " + currentNumberOfOptions.value)
          val inputField = new TextField()
          optionHashMap.put(currentNumberOfOptions.value, (label, inputField))
          pollGridPane.addRow(nextFreeIndex, label, inputField)
          nextFreeIndex += 1
        }


        currentNumberOfOptions.onChange({
          if (currentNumberOfOptions.value == maxNumberOfOptions) {
            addOptionButton.disable = true
            errorMessageBox.children.clear()
            errorMessageBox.children.add(new Text("The maximum number of options is " + maxNumberOfOptions + "!\n")): Unit
          }
        })


        val indexForAddOptionButton = maxNumberOfOptions + 2
        val indexForSubmitButton = maxNumberOfOptions + 3
        var indexForErrorMessageBox = maxNumberOfOptions + 4


        pollGridPane.addRow(indexForAddOptionButton, addOptionButton)

        val submitButton = new Button("Submit")
        submitButton.onAction = e => {
          errorMessageBox.children.clear()
          //wieder auf 'true' setzen für neuen Versuch
          pollIsValid = true

          val createdAt = Calendar.getInstance().getTime.toString

          val user: TwitterUser = Controller.getTwitterUser
          val question = questionInputField.getText

          if (question.length == 0) {
            errorMessageBox.children.add(new Text("The 'Question' input field cannot be empty!\n"))
            pollIsValid = false
          }

          val options = new ArrayBuffer[Option]

          val sortedOptions = optionHashMap.toSeq.sortWith(_._1 < _._1)

          sortedOptions.foreach(x => {
            val key = x._1
            val value = x._2
            val optionInputField = value._2


            if (optionInputField.getText.length == 0) {
              errorMessageBox.children.add(new Text("The 'Option " + key + "' input field cannot be empty!\n"))
              pollIsValid = false
            }


            options += new Option(key, optionInputField.getText, 0)

          })

          if (pollIsValid) {
            // TODO: ids generieren
            val pollID = 1

            val poll = new Poll(pollID, statement.ID, user.ID, user.userName, question, options, createdAt)
            Controller.sendPoll(poll)
            pollTemplate.children.clear()
            pollTemplateIsVisible = false
          }
        }

        pollGridPane.addRow(indexForSubmitButton, submitButton)

        pollGridPane.add(errorMessageBox, 0, indexForErrorMessageBox, 4, 1)

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
    val userList = new ListView[TwitterUser]
    userList.items = Controller.getChatMembers(statement)
    Controller.getChatMembers(statement).onChange({
      userList.items = Controller.getChatMembers(statement)
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


    val submitButton = new Button("Submit Answer")
    submitButton.disable = true

    poll.options.foreach(option => {
      rowIndex += 1
      val optionId = option.key
      val optionText = option.name
      val radioButton = new RadioButton(optionText)
      radioButton.id = optionId.toString

      radioButton.onAction = e => {
        radioButton.selected = true
        submitButton.disable = false
      }
      toggleGroup.toggles.add(radioButton)
      pollGrid.add(radioButton, columnIndex, rowIndex)
    })


    rowIndex += 1

    submitButton.onAction = e => {
      val selectedButton = toggleGroup.selectedToggle.value.asInstanceOf[javafx.scene.control.RadioButton]
      val selectedButtonId = selectedButton.getId
      val selectedButtonText = selectedButton.getText
      val stamp = Calendar.getInstance().getTime.toString
      val pollAnswer = new PollAnswer(poll.ID, poll.statementID, poll.userID, poll.userName, poll.question, (selectedButtonId.toString.toInt, selectedButtonText.toString),
        stamp)
      Controller.sendPollAnswer(pollAnswer)


      toggleGroup.toggles.forEach(toogle => {
        val radio = toogle.asInstanceOf[javafx.scene.control.RadioButton]
        radio.setDisable(true)
      })

      submitButton.disable = true
      submitButton.visible = false
    }
    pollGrid.add(submitButton, columnIndex, rowIndex)
    return pollGrid
  }

  def renderInactivePoll(): Unit = {

  }

  def renderCompletedPoll(): Unit = {

  }


  def createActivity(statement: Statement): VBox = {

    val chatRoomInformation = new GridPane()
    chatRoomInformation.addColumn(0, enterChatRoomButton(statement))


    if(Controller.statementContainsComments(statement)){
      import scalafx.geometry.Insets
      val numberOfComments = Controller.getNumberOfComments(statement)
      val commentInfo = new Text("Comments: " + numberOfComments)
      commentInfo.margin = new Insets(new javafx.geometry.Insets(5,5,5,5))
      chatRoomInformation.addColumn(1, commentInfo)
    }

    if (Controller.statementContainsPolls(statement)){
      import scalafx.geometry.Insets
      val numberOfPolls = Controller.getNumberOfPolls(statement)
      val pollInfo = new Text("Polls: " + numberOfPolls)
      pollInfo.margin = new Insets(new javafx.geometry.Insets(5,5,5,5))
      chatRoomInformation.addColumn(2, pollInfo)
    }




    val activity = new VBox(new Label(statement.userName), new Text(statement.message), chatRoomInformation)
    return activity
  }

  def addActivity(activity: VBox): Unit = {
    activityFeed += activity
  }


  def enterChatRoomButton(statement: Statement): Button = {
    import scalafx.geometry.Insets
    val enterChatRoomButton = new Button("Enter Chat Room")
    enterChatRoomButton.onAction = e => {

      //Controller.getChatRooms().add(statement)
      if (!Controller.chatRoomsContainStatement(statement)){
        Controller.setStatementInChatRooms(statement)
      }
      Controller.subscribe(statement, true)

    }
    enterChatRoomButton.margin = new Insets(new javafx.geometry.Insets(5,5,5,5))
    return enterChatRoomButton
  }


  def logoutButton(): Button = {
    val logoutButton = new Button("Logout")
    logoutButton.onAction = e => {
      Controller.logout
    }
    return logoutButton
  }
}
