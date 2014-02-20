package eu.pulsation.slicksandbox

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.{EditText, TextView}

import scala.slick.driver.SQLiteDriver.simple._
import scala.slick.jdbc.meta.MTable

import scala.concurrent._
import ExecutionContext.Implicits.global

class SlickSandbox extends Activity
{
  // Implicit conversion to Runnable when called by runOnUiThread()
  implicit def toRunnable[F](f: => F): Runnable = new Runnable() { def run() = f }

  class MyData(tag: Tag) extends Table[(Int, String)](tag, "MYDATA") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc) // This is the primary key column
    def name = column[String]("NAME")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, name)
  }
  val myData = TableQuery[MyData]
  lazy val db = Database.forURL("jdbc:sqlite:" +
    getApplicationContext().getFilesDir() +
    "slick-sandbox.txt", driver = "org.sqldroid.SQLDroidDriver")

  // Views
  lazy val mText : TextView = findViewById(R.id.text) match { case t : TextView => t }
  lazy val mEdit : EditText = findViewById(R.id.data) match { case e : EditText => e }

  def createDb() = {
    db withSession { implicit session =>
      // Create table if needed
      if (MTable.getTables("MYDATA").list().isEmpty) {
        (myData.ddl).create
      }
    }
  }

  def getRows() = {
    db withSession {
      implicit session =>
      // Get existing rows
        myData.list
    }
  }

  def displayDataList(rows : List[(Int,String)]) = {
    mText.setText("")
    rows foreach({ case (id : Int, name: String) =>
      mText.append(id +
        " " + name +
        System.getProperty("line.separator"))
    })
  }

  def saveData() = {
    db withSession {
      implicit session =>
        myData += (0, mEdit.getText().toString)
    }
  }

  def processThenDisplay(process : () => Unit) {
    val fProcessData = Future { process() }
    val fFetchData : Future[List[(Int, String)]] = fProcessData map((nothing) => {
      // This will be executed after f1 has been executed
      getRows()
    })

    fFetchData onSuccess {
      case rows =>
        runOnUiThread({ displayDataList(rows) })
    }
  }

  /** Called when the activity is first created. */
  override def onCreate(savedInstanceState : Bundle)
  {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
    processThenDisplay(createDb)
  }

  def saveDataText(view : View) {
    processThenDisplay(saveData)
  }
}
