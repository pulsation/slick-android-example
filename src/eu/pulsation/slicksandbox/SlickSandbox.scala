
// Slick example: https://stackoverflow.com/questions/21314532/slick-n-scala-a-tablequery-object-without-ddl-field
// https://github.com/slick/slick-examples/blob/master/src/main/scala/com/typesafe/slick/examples/lifted/FirstExample.scala
// doc: http://slick.typesafe.com/doc/0.11.1/gettingstarted.html

package eu.pulsation.slicksandbox

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import scala.slick.driver.SQLiteDriver.simple._
import scala.slick.jdbc.meta.MTable
import android.util.Log

class SlickSandbox extends Activity
{

  class Suppliers(tag: Tag) extends Table[(Int, String)](tag, "NAMES") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc) // This is the primary key column
    def name = column[String]("NAME")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, name)
  }
  val suppliers = TableQuery[Suppliers]
  lazy val db = Database.forURL("jdbc:sqlite:" + getApplicationContext().getFilesDir() + "slick-sandbox.txt", driver = "org.sqldroid.SQLDroidDriver")

    /** Called when the activity is first created. */
    override def onCreate(savedInstanceState : Bundle)
    {
        super.onCreate(savedInstanceState)
      setContentView(R.layout.main)
      db withSession {
        implicit session =>
        // Create table if needed
          if (MTable.getTables("NAMES").list().isEmpty) {
            (suppliers.ddl).create
          }

          // Get existing rows
          suppliers.list.foreach(supplier => android.util.Log.v("DEBUG", supplier._1 + " " + supplier._2))
      }
    }

   def saveDataText(view : View) {
   Toast.makeText(getApplicationContext(), "TODO", Toast.LENGTH_LONG).show()
     db withSession {
       implicit session =>
         //suppliers += ("Test");
         suppliers.insert(0, "Test")
     }
	// TODO
   }
}
