
// Slick example: https://stackoverflow.com/questions/21314532/slick-n-scala-a-tablequery-object-without-ddl-field

package eu.pulsation.slicksandbox

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
/*
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import scala.slick.lifted._
*/
import scala.slick.driver.SQLiteDriver.simple._

class SlickSandbox extends Activity
{

   // Definition of the COFFEES table
  /*
  class Coffees(tag: Tag) extends Table[COFFEES](tag, "COFFEES") {
    def name = column[String]("COF_NAME", O.PrimaryKey)
    def price = column[Double]("PRICE")
    def * = name ~ price
  }
  */

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
     val db = Database.forURL("jdbc:sqlite:/sdcard/slick-sandbox.txt", driver = "org.sqlite.JDBC")
     db withSession {
       implicit session =>
       val suppliers = TableQuery[Suppliers]
       suppliers.filter(_.id < 10).map(_.name).list
     }
	// TODO
   }
}
