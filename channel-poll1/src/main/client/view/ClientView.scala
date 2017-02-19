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
import scalafx.scene.paint.Color._
import scalafx.collections.ObservableMap


final object ClientView extends JFXApp {

  var tabList: ListBuffer[Tab] = null
  var currentChats: ObservableMap[Long, Statement] = null
  var tabPane: TabPane = null

  def getStage(): PrimaryStage = {
    stage = new PrimaryStage {
      title = "ChannelPoll"
      maxHeight = Double.MaxValue
      maxWidth = Double.MaxValue
      scene = new Scene {

        //TabPane
        tabPane = new TabPane()

        //General-Tab: wird immer angezeigt
        val generalTab = new Tab()
        generalTab.text = "General"
        generalTab.content = generalTabContent()

        //Liste aller Tabs
        tabList = ListBuffer(generalTab)

        currentChats = ObservableMap[Long, Statement]()

        //Eigener User
        val twitterUser = Controller.getTwitterUser


        tabPane.tabs = tabList


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
      Platform.runLater({
        val entry = createEntry(Controller.getStatements.last)
        entries.children.add(0, entry): Unit
      })
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
    import scalafx.geometry.Pos
    val border = new BorderPane()
    var pollTemplateIsVisible = false

    //Center: Content

    //Statementverfasser und -text
    val user = new Label(statement.userName)
    val message = new Text(statement.message)

    val statementGrid = new GridPane()
    statementGrid.hgap = 5
    statementGrid.vgap = 5
    statementGrid.maxWidth = Double.MaxValue
    statementGrid.addRow(0, user)
    statementGrid.addRow(1, message)


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


    //Textfeld für Kommentare
    val commentInputField = new TextArea()

    //Button zum Abschicken von Kommentaren
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

    //Vorlage für neue Umfrage
    val pollTemplate = new GridPane()
    pollTemplate.hgap = 5
    pollTemplate.vgap = 5

    //Button zum Erstellen von Umfragen
    val pollButton = new Button("Poll")
    pollButton.onAction = e => {
      if (!pollTemplateIsVisible) {
        pollTemplateIsVisible = true

        var pollIsValid = true

        //Frage
        val questionLabel = new Label("Question")
        val questionInputField = new TextField()
        questionInputField.requestFocus()

        //Minimale Anzahl an Antwortmöglichkeiten
        val minNumberOfOptions = 2
        //Maximale Anzahl an Antwortmöglichkeiten
        val maxNumberOfOptions = 5
        //Aktuelle Anzahl an Antwortmöglichkeiten
        val currentNumberOfOptions = IntegerProperty(0)

        //Anzeigefeld für Fehlermeldungen
        val warning = new Text()
        warning.fill = Red

        val optionHashMap = new HashMap[Int, (Label, TextField)]

        pollTemplate.addRow(0, questionLabel, questionInputField)

        var nextFreeIndex = 1

        //Textfelder für obligatorische Antwortmöglichkeiten
        for (i <- 1 to minNumberOfOptions) {
          val label = new Label("Option " + i)
          val inputField = new TextField()
          optionHashMap.put(i, (label, inputField))
          pollTemplate.addRow(nextFreeIndex, label, inputField)
          currentNumberOfOptions.value = currentNumberOfOptions.value + 1
          nextFreeIndex += 1
        }


        //Button für zusätzliche Antwortmöglichkeiten
        val addOptionButton = new Button("Add Option")
        addOptionButton.onAction = e => {
          currentNumberOfOptions.value = currentNumberOfOptions.value + 1
          val label = new Label("Option " + currentNumberOfOptions.value)
          val inputField = new TextField()
          optionHashMap.put(currentNumberOfOptions.value, (label, inputField))
          pollTemplate.addRow(nextFreeIndex, label, inputField)
          nextFreeIndex += 1
        }


        //Prüft, ob die maximale Anzahl and Antwortmöglichkeiren erreicht wurde
        currentNumberOfOptions.onChange({
          if (currentNumberOfOptions.value == maxNumberOfOptions) {
            addOptionButton.disable = true
            warning.text = "The maximum number of options is " + maxNumberOfOptions + "!\n"
          }
        })


        val indexForButtons = maxNumberOfOptions + 2
        val indexForWarningMessages = maxNumberOfOptions + 3


        val submitButton = new Button("Submit")
        submitButton.onAction = e => {
          warning.text = ""
          //wieder auf 'true' setzen für neuen Versuch
          pollIsValid = true

          val createdAt = Calendar.getInstance().getTime.toString

          val user: TwitterUser = Controller.getTwitterUser
          val question = questionInputField.getText

          if (question.length == 0) {
            warning.text = "Complete the fields!\n"
            pollIsValid = false
          }

          val options = new ArrayBuffer[Option]

          val sortedOptions = optionHashMap.toSeq.sortWith(_._1 < _._1)

          sortedOptions.foreach(x => {
            val key = x._1
            val value = x._2
            val optionInputField = value._2


            if (optionInputField.getText.length == 0) {
              warning.text = "Complete the fields!\n"
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

        pollTemplate.addRow(indexForButtons, addOptionButton, submitButton)
        pollTemplate.add(warning, 0, indexForWarningMessages, 2, 1)

      }
    }


    commentInputField.maxWidth = Double.MaxValue

    val inputGrid = new GridPane()
    inputGrid.vgap = 5
    inputGrid.hgap = 5

    inputGrid.add(commentInputField, 0, 0, 2, 2)

    sendButton.maxWidth = Double.MaxValue
    pollButton.maxWidth = Double.MaxValue
    inputGrid.add(sendButton, 2, 0, 1, 1)
    inputGrid.add(pollButton, 2, 1, 1, 1)
    inputGrid.add(pollTemplate, 0, 2, 2, 1)
    inputGrid.maxHeight = Double.MinValue
    inputGrid.maxWidth = Double.MaxValue
    inputGrid.alignment = Pos.BottomLeft

    scrollPane.maxHeight = Double.MaxValue
    scrollPane.maxWidth = Double.MaxValue


    val chatGrid = new GridPane()
    chatGrid.vgap = 15
    chatGrid.hgap = 15
    chatGrid.maxWidth = Double.MaxValue
    chatGrid.margin = Insets(15)
    chatGrid.addRow(0, statementGrid)
    chatGrid.addRow(1, scrollPane)
    chatGrid.addRow(2, inputGrid)

    border.center = chatGrid


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


  //Erstellt einen Eintrag für einen Kommentar und gibt diesen zurück
  def renderComment(comment: Comment): GridPane = {
    val commentGrid = new GridPane()
    commentGrid.margin = Insets(10)
    val user = new Text(comment.userName)
    val message = new Text(comment.message)
    commentGrid.addRow(0, user)
    commentGrid.addRow(1, message)
    return commentGrid
  }

  //Erstellt einen Eintrag für eine aktive Umfrage (die noch nicht beantwortet wurde)
  def renderActivePoll(statement: Statement, poll: Poll): GridPane = {


    val pollGrid = new GridPane()
    pollGrid.margin = Insets(10)
    val user = new Label(Controller.getTwitterUser.userName)
    val question = new Text(poll.question)
    val options = new GridPane()
    options.vgap = 5
    options.hgap = 5

    val toggleGroup = new ToggleGroup()

    val submitButton = new Button("Submit Answer")
    submitButton.disable = true


    val resultButton = new Button("Show Results")
    resultButton.disable = true


    resultButton.onAction = e => {

      val updatedPoll = getUpdatedPoll(statement, poll.ID)


      pollGrid.children.remove(4)
      pollGrid.children.remove(3)
      pollGrid.children.remove(2)

      var results = getPollResults(updatedPoll)
      pollGrid.addRow(3, results)


      val updateButton = updateResultsButton()

      updateButton.onAction = e => {
        val updatedPoll = getUpdatedPoll(statement, poll.ID)
        pollGrid.children.remove(3)
        pollGrid.children.remove(2)
        results = getPollResults(updatedPoll)
        pollGrid.addRow(3, results)
        pollGrid.addRow(4, updateButton)
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
    pollGrid.addRow(1, question)
    pollGrid.addRow(2, options)
    pollGrid.addRow(3, submitButton, resultButton)

    pollGrid.vgap = 5
    pollGrid.hgap = 5

    return pollGrid
  }


  //Erstellt einen Eintrag für eine Umfrage, die bereits beantwortet wurde
  def renderCompletedPoll(statement: Statement, poll: Poll): GridPane = {

    val pollGrid = new GridPane()
    pollGrid.margin = Insets(10)
    val user = new Label(Controller.getTwitterUser.userName)
    val question = new Text(poll.question)
    var results = getPollResults(poll)


    val updateButton = updateResultsButton()
    updateButton.onAction = e => {
      val updatedPoll = getUpdatedPoll(statement, poll.ID)
      results = getPollResults(updatedPoll)
    }

    pollGrid.addRow(0, user)
    pollGrid.addRow(1, question)
    pollGrid.addRow(2, results)
    pollGrid.addRow(3, updateButton)

    pollGrid.vgap = 5
    pollGrid.hgap = 5

    return pollGrid
  }


  //Button zum Updaten von Umfrageergebnissen
  def updateResultsButton(): Button = {
    new Button("Update Results")
  }

  //Gibt Umfrageergebnisse zurück
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

    results.vgap = 10
    results.hgap = 10

    return results
  }

  //Gibt den aktuellen Zustand einer Umfrage zurück (mit aktuellen Ergebnissen)
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

    var numberOfComments: Int = 0
    var commentInfo: Text = null

    var numberOfPolls: Int = 0
    var pollInfo: Text = null


    //Falls Chatroom bereits Kommentare enthält, soll deren Anzahl angezeigt werden
    if (Controller.statementContainsComments(statement)) {
      Platform.runLater {
        numberOfComments = Controller.getNumberOfComments(statement)
        commentInfo = new Text("Comments: " + numberOfComments)
        chatRoomInformation.addColumn(1, commentInfo)
      }
    }


    Controller.getCommentsForStatement(statement).onChange({
      Platform.runLater {
        numberOfComments = Controller.getNumberOfComments(statement)
        commentInfo = new Text("Comments: " + numberOfComments)
        chatRoomInformation.children.clear()
        chatRoomInformation.addColumn(0, enterChatRoomButton(statement))
        chatRoomInformation.addColumn(1, commentInfo)
        chatRoomInformation.addColumn(2, pollInfo)
      }
    })


    //Falls Chatroom bereits Umfragen enthält, soll deren Anzahl angezeigt werden
    if (Controller.statementContainsPolls(statement)) {
      numberOfPolls = Controller.getNumberOfPolls(statement)
      pollInfo = new Text("Polls: " + numberOfPolls)
      chatRoomInformation.addColumn(2, pollInfo)
    }

    Controller.getPollsForStatement(statement).onChange({
      Platform.runLater {
        numberOfPolls = Controller.getNumberOfPolls(statement)
        pollInfo = new Text("Polls: " + numberOfPolls)
        chatRoomInformation.children.clear()
        chatRoomInformation.addColumn(0, enterChatRoomButton(statement))
        chatRoomInformation.addColumn(1, commentInfo)
        chatRoomInformation.addColumn(2, pollInfo)
      }
    })


    //Eintrag soll Statementverfasser, -text und die oben erzeugten Infos enthalten
    val entry = new GridPane()
    entry.addRow(0, new Label(statement.userName))
    entry.addRow(1, new Text(statement.message))
    entry.addRow(2, chatRoomInformation)

    entry.margin = Insets(15)
    entry.vgap = 10
    entry.hgap = 10

    return entry
  }


  //Button zum Betreten eines Chatroom (Öffnen eines Tabs)
  def enterChatRoomButton(statement: Statement): Button = {
    val enterChatRoomButton = new Button("Enter Chat Room")
    enterChatRoomButton.onAction = e => {
      Controller.getChatRooms.add(statement)

      if (!currentChats.contains(statement.ID)) {
        currentChats.put(statement.ID, statement)

        val statementTab = new Tab()
        statementTab.id = statement.ID.toString
        statementTab.text = statement.userName
        statementTab.content = statementTabContent(statement)
        statementTab.onClosed = e => {
          currentChats.remove(statementTab.id.value.toLong)
          tabList -= statementTab
          tabPane.tabs = tabList
        }
        tabList += statementTab
        tabPane.tabs = tabList

      }

    }
    return enterChatRoomButton
  }


  //Logout-Button
  def logoutButton(): Button = {
    val logoutButton = new Button("Logout")
    logoutButton.margin = Insets(3)
    logoutButton.onAction = e => {
      Controller.logout
    }
    return logoutButton
  }


  //Gibt das Datum eines Objektes (Comment oder Poll) zurück
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
