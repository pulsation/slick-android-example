# Slick Android Example

A quick example of [Scala](http://www.scala-lang.org/) and [Slick](http://slick.typesafe.com/) running on the [Android](https://developer.android.com/sdk/index.html) platform.

## How to build

First, you have to configure the android platform target:

	$ android update project -p . -t <target>

Then, you can build the project:

	$ sbt android:package

Optionally, you could generate an IntelliJ project for these sources:

	$ sbt gen-idea
