import android.Keys._

android.Plugin.androidBuild

name := "SlickSandbox"

libraryDependencies += "org.slf4j" % "slf4j-android" % "1.6.1-RC1"

libraryDependencies += "com.typesafe.slick" %% "slick" % "2.0.0"

proguardOptions in Android ++= Seq(
  "-dontwarn javax.naming.InitialContext",
  "-dontnote org.slf4j.**"
)
