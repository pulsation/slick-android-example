package eu.pulsation.slickexample

import scala.concurrent._
import scala.language.implicitConversions
import ExecutionContext.Implicits.global

import scala.slick.driver.SQLiteDriver.simple._
import scala.slick.jdbc.meta.MTable

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.{Toast, EditText, TextView}
import android.text.method.ScrollingMovementMethod

class SlickAndroidExample extends Activity
{

  // Table name in the SQL database
  final val TableName = "MY_DATA"

  // Table definition
  class MyData(tag: Tag) extends Table[(Int, String)](tag, TableName) {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc) // This is the primary key column.
    def name = column[String]("SOME_TEXT")
    // Every table needs a * projection with the same type as the table's type parameter.
    def * = (id, name)
  }

  // Table representation instance
  val myData = TableQuery[MyData]

  // Database connection
  lazy val db = Database.forURL("jdbc:sqlite:" +
    getApplicationContext().getFilesDir() +
    "slick-sandbox.txt", driver = "org.sqldroid.SQLDroidDriver")

  // Views
  lazy val mText : TextView = findViewById(R.id.text) match { case t : TextView => t }
  lazy val mEdit : EditText = findViewById(R.id.data) match { case e : EditText => e }

  /**
   * Create the table if needed
   */
  def createTable() = {
    db withSession { implicit session =>
      if (MTable.getTables(TableName).list().isEmpty) {
        myData.ddl.create
      }
    }
  }

  /**
   * Fetch all rows
   * @return rows
   */
  def fetchRows() = {
    db withSession {
      implicit session =>
      // Get existing rows
        myData.list
    }
  }

  /**
   * Display data in text view
   * @param rows Data to display
   */
  def displayDataList(rows : List[(Int,String)]) = {
    mText.setText("")
    rows foreach({ case (id : Int, name: String) =>
      mText.append(id +
        ". " + name +
        System.getProperty("line.separator"))
    })
  }

  /**
   * Add one row to table
   */
  def saveData() : Unit = {
    // This is an example usage of an implicit database session.
    db withSession {
      implicit session =>
        // Add a row to the existing data set
        myData += (0, mEdit.getText().toString)
    }
  }

  /**
   * Remove data from table
   */
  def clearData() : Unit = {
    // In opposition to saveData(), this is an example of using
    // an explicit session. It could have been implicit as well.
    val session = db.createSession()
    // Delete all rows
    myData.delete(session)
  }

  // Implicit conversion to Runnable when called by runOnUiThread().
  implicit def toRunnable[F](f: => F): Runnable = new Runnable() { def run() = f }

  /**
   * Process data, then fetch all data to update the UI.
   * @param process
   */
  def processThenDisplay(process : () => Unit) : Future[List[(Int, String)]] = {
    val fProcessData = Future { process() }
    val fFetchData : Future[List[(Int, String)]] = fProcessData map((nothing) => {
      // This will be executed after data has been processed.
      fetchRows()
    })

    fFetchData onSuccess {
      case rows =>
        runOnUiThread({ displayDataList(rows) })
    }
    fFetchData
  }

  // Initialize table
  lazy val initFuture = processThenDisplay(createTable)

  /**
   * Called when the activity is first created.
   */
  override def onCreate(savedInstanceState : Bundle)
  {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
    mText.setMovementMethod(new ScrollingMovementMethod())

    initFuture
  }

  /**
   * Displays an error message
   */
  def notifyError() {
    Toast.makeText(getApplicationContext(), "Database not initialized yet", Toast.LENGTH_SHORT).show()
  }

  /**
   * Clear table data
   */
  def clearText(view : View) {
    if (initFuture.isCompleted) {
      processThenDisplay(clearData)
    } else {
      notifyError()
    }
  }

  /**
   * Insert edit text contents into database
   */
  def addContent(view : View) {
    if (initFuture.isCompleted) {
      processThenDisplay(saveData)
    } else {
      notifyError()
    }
  }
}
