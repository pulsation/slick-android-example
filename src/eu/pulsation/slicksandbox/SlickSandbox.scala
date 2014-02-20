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
    db withSession {
      implicit session =>
      // Create table if needed
        if (MTable.getTables("MYDATA").list().isEmpty) {
          (myData.ddl).create
        }
    }
  }

  def getRows() = {
    mText.setText("")
    db withSession {
      implicit session =>
      // Get existing rows
        myData.list map(e => (e._1, e._2)) /*.foreach(
          row =>
            mText.append(row._1 +
              " " + row._2 +
              System.getProperty("line.separator"))
        )*/
    }
  }

  /** Called when the activity is first created. */
  override def onCreate(savedInstanceState : Bundle)
  {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
    val f1 = Future { createDb() }
    val f2 : Future[List[(Int, String)]] = f1 map {
      nothing =>
      getRows()
    }
    f2 onSuccess {
      case l =>
        runOnUiThread({
        l foreach( row =>
          mText.append(row._1 +
            " " + row._2 +
            System.getProperty("line.separator"))
          )
        })
    }
  }

  def saveDataText(view : View) {
    db withSession {
      implicit session =>
        myData += (0, mEdit.getText().toString)
    }
  }
}
