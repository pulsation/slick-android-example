import android.Keys._

android.Plugin.androidBuild

name := "SlickSandbox"


libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.slick" %% "slick" % "2.0.0"
)

proguardOptions in Android ++= Seq(
  "-dontwarn javax.naming.InitialContext",
  "-dontnote org.slf4j.**",
  "-keep class scala.collection.Seq.**",
  "-keep public class org.sqldroid.**",
  "-keep class scala.concurrent.Future$.**"
)

proguardCache in Android ++= Seq(
  ProguardCache("slick") % "com.typesafe.slick"
)
