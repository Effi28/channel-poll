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
import scalafx.geometry.Insets
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control._


final object ClientView extends JFXApp {
  var pollTemplateIsVisible = false

  def getStage(): PrimaryStage = {
    stage = new PrimaryStage {
      title = "ChannelPoll"
      maxHeight = Double.MaxValue
      maxWidth = Double.MaxValue
      scene = new Scene {

        //TabPane
        val tabPane = new TabPane()

        //general tab: wird immer angezeigt
        val generalTab = new Tab()
        generalTab.text = "General"
        generalTab.content = generalTabContent()

        //liste aller tabs
        val tabList = ListBuffer(generalTab)

        //eigener user
        val twitterUser = Controller.getTwitterUser

        //future work: zukünftig sollen alle chats, zu denen der user angemeldet ist, in form von tabs bereits geöffnet sein (tbd)
        if (Controller.getUserChatRooms(twitterUser).size > 0) {
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

        //wenn der user einen chatroom betritt, soll dieser in form eines tabs geöffnet werden
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

        //Root
        root = tabPane
      }

    }
    stage
  }


  //erzeugt den inhalt des general tabs und gibt diesen zurück
  def generalTabContent(): BorderPane = {

    val border = new BorderPane()

    //Center: Content
    val entries = new VBox()
    entries.spacing = 10

    //falls statements bereits vorhanden sind, sollen einträge dafür erzeugt werden
    if (Controller.getStatements.size > 0) {
      Controller.getStatements.foreach(st => {
        val entry = createEntry(st)
        entries.children.add(0, entry)
      })
    }

    //falls neue statements hinzukommen, sollen einträge dafür erzeugt werden
    Controller.getStatements.onChange({
      val entry = createEntry(Controller.getStatements.last)
      entries.children.add(0, entry): Unit
    })


    //ScrollPane mit vertikaler ScrollBar
    val scroll = new ScrollPane()
    scroll.content = entries
    scroll.hbarPolicy = ScrollBarPolicy.Never
    border.center = scroll


    //Right: User (online)
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
        var userHasAlreadyAnswered = false
        poll.options.foreach(option => {
          option.likeUser.foreach(user => {
            if (user == Controller.getTwitterUser.userName) {
              userHasAlreadyAnswered = true
            }
          })

        })


        if (userHasAlreadyAnswered) {
          chatFeed.children.add(renderCompletedPoll(statement, poll))
        }
        else {
          chatFeed.children.add(renderActivePoll(statement, poll))
        }
      })
    }

    Controller.getPollsForStatement(statement).onChange({
      Platform.runLater {
        val latestPoll = Controller.getPollsForStatement(statement).last
        val poll = renderActivePoll(statement, latestPoll)
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


        val userID = Controller.getTwitterUser.ID
        val userName = Controller.getTwitterUser.userName

        var commentCounter = 0
        if (Controller.statementContainsComments(statement)) {
          commentCounter = Controller.getNumberOfComments(statement)
        }
        val commentID = (statement.ID.toString + commentCounter.toString).toLong
        val comment = new Comment(commentID, statement.ID, userID, userName, message, createdAt)
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


            options += new Option(key, optionInputField.getText, 0, new ArrayBuffer[String]())

          })

          if (pollIsValid) {
            var pollCounter = 0
            if (Controller.statementContainsPolls(statement)) {
              pollCounter = Controller.getNumberOfPolls(statement)
            }
            val pollID = (statement.ID.toString + pollCounter.toString).toLong
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
    val user = new Text(comment.userName)
    val message = new Text(comment.message)
    commentGrid.addRow(0, user)
    commentGrid.addRow(1, message)
    return commentGrid
  }

  def renderActivePoll(statement: Statement, poll: Poll): GridPane = {


    var pollGrid = new GridPane()
    val user = new Text(Controller.getTwitterUser.userName)
    val header = new Label("Poll")
    val question = new Text(poll.question)
    val options = new GridPane()

    val toggleGroup = new ToggleGroup()

    val submitButton = new Button("Submit Answer")
    submitButton.disable = true


    val resultButton = new Button("Show Results")
    resultButton.disable = true


    resultButton.onAction = e => {

      val updatedPoll = getUpdatedPoll(statement, poll.ID)


      pollGrid.children.remove(5)
      pollGrid.children.remove(4)
      pollGrid.children.remove(3)

      var results = getPollResults(updatedPoll)
      pollGrid.addRow(3, results)


      val updateButton = updateResultsButton()

      updateButton.onAction = e => {
        val updatedPoll = getUpdatedPoll(statement, poll.ID)
        results = getPollResults(updatedPoll)
      }

      pollGrid.addRow(4, updateButton)

    }


    submitButton.onAction = e => {
      val selectedButton = toggleGroup.selectedToggle.value.asInstanceOf[javafx.scene.control.RadioButton]
      val selectedButtonId = selectedButton.getId
      val selectedButtonText = selectedButton.getText
      val stamp = Calendar.getInstance().getTime.toString
      val pollAnswer = new PollAnswer(poll.ID, poll.statementID, poll.userID, poll.userName, poll.question, (selectedButtonId.toString.toInt, selectedButtonText.toString),
        stamp)

      toggleGroup.toggles.forEach(toggle => {
        val radio = toggle.asInstanceOf[javafx.scene.control.RadioButton]
        radio.setDisable(true)
      })

      Controller.sendPollAnswer(pollAnswer)

      submitButton.disable = true
      resultButton.disable = false


    }


    var rowIndex = 0

    poll.options.foreach(option => {

      val key = option.key
      val answer = option.name
      val radioButton = new RadioButton(answer)
      radioButton.id = key.toString

      radioButton.onAction = e => {
        radioButton.selected = true
        submitButton.disable = false
      }
      toggleGroup.toggles.add(radioButton)

      options.addRow(rowIndex, radioButton)
      rowIndex += 1

    })


    pollGrid.addRow(0, user)
    pollGrid.addRow(1, header)
    pollGrid.addRow(2, question)
    pollGrid.addRow(3, options)
    pollGrid.addRow(4, submitButton, resultButton)

    return pollGrid
  }


  def renderCompletedPoll(statement: Statement, poll: Poll): GridPane = {

    val pollGrid = new GridPane()
    val user = new Text(Controller.getTwitterUser.userName)
    val header = new Label("Poll")
    val question = new Text(poll.question)
    var results = getPollResults(poll)


    val updateButton = updateResultsButton()

    updateButton.onAction = e => {
      val updatedPoll = getUpdatedPoll(statement, poll.ID)
      results = getPollResults(updatedPoll)
    }

    pollGrid.addRow(0, user)
    pollGrid.addRow(1, header)
    pollGrid.addRow(2, question)
    pollGrid.addRow(3, results)
    pollGrid.addRow(4, updateButton)

    return pollGrid
  }


  def updateResultsButton(): Button = {
    new Button("Update Results")
  }

  def getPollResults(poll: Poll): GridPane = {
    val results = new GridPane()

    var rowIndex = 0
    poll.options.foreach(option => {
      val key = option.key
      val answer = new Label(option.name)
      val votes = new Text(option.likes.toString)
      val voter = option.likeUser

      results.add(answer, 1, rowIndex)
      results.add(votes, 2, rowIndex)
      rowIndex += 1

    })
    return results
  }

  def getUpdatedPoll(statement: Statement, pollID: Long): Poll = {
    var updatedPoll: Poll = null
    Controller.getPollsForStatement(statement).foreach(p => {
      if (p.ID == pollID) {
        updatedPoll = p

      }
    })
    return updatedPoll
  }


  //erzeugt einen eintrag für ein statement und gibt diesen zurück
  def createEntry(statement: Statement): GridPane = {

    //grid welches einen button zum betreten des chatrooms sowie informationen über die anzahl der bestehenden kommentare und umfragen enthält
    val chatRoomInformation = new GridPane()
    chatRoomInformation.hgap = 10
    chatRoomInformation.vgap = 10
    chatRoomInformation.addColumn(0, enterChatRoomButton(statement))


    //falls chatroom bereits kommentare enthält, soll deren anzahl angezeigt werden
    if (Controller.statementContainsComments(statement)) {
      var numberOfComments = Controller.getNumberOfComments(statement)
      val commentInfo = new Text("Comments: " + numberOfComments)
      chatRoomInformation.addColumn(1, commentInfo)
    }


    //falls chatroom bereits umfragen enthält, soll deren anzahl angezeigt werden
    if (Controller.statementContainsPolls(statement)) {
      val numberOfPolls = Controller.getNumberOfPolls(statement)
      val pollInfo = new Text("Polls: " + numberOfPolls)
      chatRoomInformation.addColumn(2, pollInfo)
    }


    //eintrag soll statement verfasser, statement text und die oben erzeugten infos enthalten
    val entry = new GridPane()
    entry.addRow(0, new Label(statement.userName))
    entry.addRow(1, new Text(statement.message))
    entry.addRow(2, chatRoomInformation)

    entry.margin = Insets(10)

    return entry
  }

  /*
  def addEntry(activity: GridPane): Unit = {
    activityFeed += activity
  }*/


  def enterChatRoomButton(statement: Statement): Button = {
    import scalafx.geometry.Insets
    val enterChatRoomButton = new Button("Enter Chat Room")
    enterChatRoomButton.onAction = e => {

      //Controller.getChatRooms().add(statement)
      if (!Controller.chatRoomsContainStatement(statement)) {
        Controller.setStatementInChatRooms(statement)
      }
      Controller.subscribe(statement, true)

    }
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
