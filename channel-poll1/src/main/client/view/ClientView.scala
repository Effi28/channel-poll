package main.client.view


import java.util.Calendar
import java.util.Date
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
import java.text.SimpleDateFormat
import java.util.Locale


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

        //General-Tab: wird immer angezeigt
        val generalTab = new Tab()
        generalTab.text = "General"
        generalTab.content = generalTabContent()

        //Liste aller Tabs
        val tabList = ListBuffer(generalTab)

        //Eigener User
        val twitterUser = Controller.getTwitterUser

        //Future Work: zukünftig sollen alle Chats, zu denen der User angemeldet ist, in Form von Tabs bereits geöffnet sein (tbd)
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

        //Wenn der User einen Chatroom betritt, soll dieser in Form eines Tabs geöffnet werden
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


  //Erzeugt den Inhalt des General-Tabs und gibt diesen zurück
  def generalTabContent(): BorderPane = {

    val border = new BorderPane()

    //Center: Content
    val entries = new VBox()
    entries.spacing = 10

    //Falls Statements bereits vorhanden sind, sollen Einträge dafür erzeugt werden
    if (Controller.getStatements.size > 0) {
      Controller.getStatements.foreach(st => {
        val entry = createEntry(st)
        entries.children.add(0, entry)
      })
    }

    //Falls neue Statements hinzukommen, sollen Einträge dafür erzeugt werden
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


  //Erzeugt den Inhalt eines Statement-Tabs (Chats) und gibt diesen zurück
  def statementTabContent(statement: Statement): BorderPane = {
    val border = new BorderPane()


    //Center: Content

    //Statementverfasser und -text
    val user = new Label(statement.userName)
    val message = new Text(statement.message)

    //Einträge
    val chatEntries = new VBox()
    chatEntries.spacing = 10


    //ScrollPane mit vertikaler ScrollBar
    val scrollPane = new ScrollPane()
    scrollPane.content = chatEntries
    scrollPane.hbarPolicy = ScrollBarPolicy.Never

    //Liste für vorhandene Einträge (unsortiert)
    var allEntries = List[Object]()

    //Prüft, ob bereits Comments für dieses Statement vorhanden sind und fügt diese der Liste hinzu
    if (Controller.getCommentsForStatement(statement).size > 0) {
      Controller.getCommentsForStatement(statement).foreach(comment => {
        allEntries = comment :: allEntries
      })
    }

    //Prüft, ob berits Polls für dieses Statement vorhanden sind und fügt diese der Liste hinzu
    if (Controller.getPollsForStatement(statement).size > 0) {
      Controller.getPollsForStatement(statement).foreach(poll => {
        allEntries = poll :: allEntries
      })
    }

    //Listeneinträge werden in chronologischer Reihenfolge sortiert
    val sortedEntries = allEntries.sortBy(entry => getDate(entry))

    //Einträge werden in chronologischer Reihenfolge angezeigt
    sortedEntries.foreach(entry => {
      if (entry.isInstanceOf[Comment]) {
        val commentObj = entry.asInstanceOf[Comment]
        chatEntries.children.add(renderComment(commentObj))
      }
      else if (entry.isInstanceOf[Poll]) {
        val pollObj = entry.asInstanceOf[Poll]
        var userHasAlreadyAnswered = false
        //Prüft, ob dieser User diese Umfrage bereits beantwortet hat und zeigt die Umfrage abhängig davon unterschiedlich an
        pollObj.options.foreach(option => {
          option.likeUser.foreach(user => {
            if (user == Controller.getTwitterUser.userName) {
              userHasAlreadyAnswered = true
            }
          })
        })
        if (userHasAlreadyAnswered) {
          chatEntries.children.add(renderCompletedPoll(statement, pollObj))
        }
        else {
          chatEntries.children.add(renderActivePoll(statement, pollObj))
        }
      }
    })


    //Falls neue Comments hinzukommen, werden diese in der View angezeigt
    Controller.getCommentsForStatement(statement).onChange({
      Platform.runLater {
        val latestComment = Controller.getCommentsForStatement(statement).last
        val comment = renderComment(latestComment)
        chatEntries.children.add(comment)
      }
    })

    //Falls neue Polls hinzukommen, werden diese in der View angezeigt
    Controller.getPollsForStatement(statement).onChange({
      Platform.runLater {
        val latestPoll = Controller.getPollsForStatement(statement).last
        val poll = renderActivePoll(statement, latestPoll)
        chatEntries.children.add(poll)
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


  //Erzeugt einen Eintrag für ein Statement und gibt diesen zurück
  def createEntry(statement: Statement): GridPane = {

    //Grid, welches einen Button zum Betreten des Chatrooms, sowie Informationen über die Anzahl der bestehenden Kommentare und Umfragen enthält
    val chatRoomInformation = new GridPane()
    chatRoomInformation.hgap = 10
    chatRoomInformation.vgap = 10
    chatRoomInformation.addColumn(0, enterChatRoomButton(statement))


    //Falls Chatroom bereits Kommentare enthält, soll deren Anzahl angezeigt werden
    if (Controller.statementContainsComments(statement)) {
      var numberOfComments = Controller.getNumberOfComments(statement)
      val commentInfo = new Text("Comments: " + numberOfComments)
      chatRoomInformation.addColumn(1, commentInfo)
    }


    //Falls Chatroom bereits Umfragen enthält, soll deren Anzahl angezeigt werden
    if (Controller.statementContainsPolls(statement)) {
      val numberOfPolls = Controller.getNumberOfPolls(statement)
      val pollInfo = new Text("Polls: " + numberOfPolls)
      chatRoomInformation.addColumn(2, pollInfo)
    }


    //Eintrag soll Statementverfasser, -text und die oben erzeugten Infos enthalten
    val entry = new GridPane()
    entry.addRow(0, new Label(statement.userName))
    entry.addRow(1, new Text(statement.message))
    entry.addRow(2, chatRoomInformation)

    entry.margin = Insets(15)
    entry.vgap = 10

    return entry
  }


  //Button zum Betreten eines Chatroom (Öffnen eines Tabs)
  def enterChatRoomButton(statement: Statement): Button = {
    val enterChatRoomButton = new Button("Enter Chat Room")
    enterChatRoomButton.onAction = e => {
      if (!Controller.chatRoomsContainStatement(statement)) {
        Controller.setStatementInChatRooms(statement)
      }
      Controller.subscribe(statement, true)
    }
    return enterChatRoomButton
  }


  //Logout-Button
  def logoutButton(): Button = {
    val logoutButton = new Button("Logout")
    logoutButton.onAction = e => {
      Controller.logout
    }
    return logoutButton
  }


  def getDate(obj: Object): Date = {
    var stamp: String = null
    if (obj.isInstanceOf[Comment]) {
      val comment = obj.asInstanceOf[Comment]
      stamp = comment.timestamp
    }
    else if (obj.isInstanceOf[Poll]) {
      val poll = obj.asInstanceOf[Poll]
      stamp = poll.timestamp
    }
    val sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
      Locale.ENGLISH)
    val date = sdf.parse(stamp)
    return date
  }


}
