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

More information is avaliable on pfn's [Android SDK plugin for SBT](https://github.com/pfn/android-sdk-plugin).
