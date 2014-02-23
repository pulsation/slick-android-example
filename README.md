#Slick Android Example

##Setup

Before compiling this quick example, you have to install [SBT](http://www.scala-sbt.org/) and the [Android SDK](https://developer.android.com/sdk/index.html).

You can list available Android targets by issuing the following command : 

```
$ android list targets
```

Then, you can pick up one for this project with :

```
$ android update project -p . -t <target>
```

After that, you can connect your phone or launch an emulator, and run this example by executing : 

```
$ sbt android:run
```

Now, let's jump into the code.

##Table definition

In this example, a simple table containing 2 fields is defined. ID is its primary key, and SOME_TEXT contains a line of text.

```
class MyData(tag: Tag) extends Table[(Int, String)](tag, TableName) {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc) // This is the primary key column.
  def name = column[String]("SOME_TEXT")
  // Every table needs a * projection with the same type as the table's type parameter.
  def * = (id, name)
}
```

More information is available on Slick documentation's [Getting Started](http://slick.typesafe.com/doc/2.0.0/gettingstarted.html#schema) section.

##Connecting to the SQLite database

[SQLDroid](https://github.com/SQLDroid/SQLDroid) is used here as a JDBC driver provider for SQLite on Android. [getApplicationContext().getFilesDir()](https://developer.android.com/reference/android/content/Context.html#getFilesDir%28%29) points to a directory that is writable by our application.

```
lazy val db = Database.forURL("jdbc:sqlite:" +
    getApplicationContext().getFilesDir() +
    "slick-sandbox.txt", driver = "org.sqldroid.SQLDroidDriver")
```

##Table creation

The table instance's ddl component represents the table's schema description, and is used to create this schema in the SQL database.

```
myData.ddl.create
```

##Manipulating data

Data can be manipulated with Slick in a similar way to common Scala collections. For more information, you can watch [Stefan Zeiger's slick webinar](https://www.youtube.com/watch?v=53tBbl8Ovbc).

### Explicit session

In this case, the session is explicitly passed as a parameter to the function that manipulates data.

```
val session = db.createSession()
// Delete all rows
myData.delete(session)
```

### Implicit session

Here, instructions are wrapped in a ```withSession {}``` block so that the session is implicitely available while manipulating data.

```
db withSession {
  implicit session =>
  // Get existing rows
    myData.list
}
```

### Auto Incremented column

As the ID column is marked as *AutoInc*, its value (0 here) will be ignored and replaced.

```
db withSession {
  implicit session =>
  	// Add a row to the existing data set
    myData += (0, mEdit.getText().toString)
}
```

##Implicit conversion to Runnable

In Android, the UI can only be updated in the UI thread. In Java, this would be expressed this way:

```
runOnUiThread(new Runnable() {
  public void run() {
    ...
  }
});
```

In order to minimize the needed amount of code, it is possible in Scala to implicitly convert our Activity class into a Runnable whenever one if its method is called as a parameter of ```runOnUiThread()```.

```
implicit def toRunnable[F](f: => F): Runnable = new Runnable() { def run() = f }
```

As a result, any method can now be directly called in the UI thread.

```
runOnUiThread({ displayDataList(rows) })
```

##Futures

Executing blocking tasks on the main UI thread would make the application unresponsive. As an alternative to Android's [AsyncTask](https://developer.android.com/reference/android/os/AsyncTask.html), it is possible to wrap blocking tasks in Scala [Futures](http://docs.scala-lang.org/overviews/core/futures.html).

```
val fProcessData = Future { process() }
```

The ```onSuccess {}``` block will be executed once a future is completed and returned a result.

```
fFetchData onSuccess {
    case rows =>
      runOnUiThread({ displayDataList(rows) })
}
```

