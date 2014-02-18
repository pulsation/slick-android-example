
// Slick example: https://stackoverflow.com/questions/21314532/slick-n-scala-a-tablequery-object-without-ddl-field
// https://github.com/slick/slick-examples/blob/master/src/main/scala/com/typesafe/slick/examples/lifted/FirstExample.scala
// doc: http://slick.typesafe.com/doc/0.11.1/gettingstarted.html

package eu.pulsation.slicksandbox

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import scala.slick.driver.SQLiteDriver.simple._

class SlickSandbox extends Activity
{

  class Suppliers(tag: Tag) extends Table[(Int, String, String, String, String, String)](tag, "SUPPLIERS") {
    def id = column[Int]("SUP_ID", O.PrimaryKey) // This is the primary key column
    def name = column[String]("SUP_NAME")
    def street = column[String]("STREET")
    def city = column[String]("CITY")
    def state = column[String]("STATE")
    def zip = column[String]("ZIP")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, name, street, city, state, zip)
  }
  val suppliers = TableQuery[Suppliers]

    /** Called when the activity is first created. */
    override def onCreate(savedInstanceState : Bundle)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
    }

   def saveDataText(view : View) {
   Toast.makeText(getApplicationContext(), "TODO", Toast.LENGTH_LONG).show()
     val db = Database.forURL("jdbc:sqlite:" + getApplicationContext().getFilesDir() + "slick-sandbox.txt", driver = "org.sqldroid.SQLDroidDriver")
     db withSession {
       implicit session =>
       val suppliers = TableQuery[Suppliers]
       suppliers.filter(_.id < 10).map(_.name).list
     }
	// TODO
   }
}
