name := "JIAccredsBackend"
 
version := "1.0" 
      
lazy val `jiaccredsbackend` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  ehcache, ws, specs2 % Test, guice,
  "com.typesafe.play" %% "play-slick" % "3.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0",

  // Database drivers
  "mysql" % "mysql-connector-java" % "5.1.34",
  "org.mariadb.jdbc" % "mariadb-java-client" % "1.1.7",

  // Tokens
  "com.pauldijou" %% "jwt-play" % "0.16.0",

  // Utils
  "net.codingwell" %% "scala-guice" % "4.1.0",
  "ch.japanimpact" %% "jiauthframework" % "0.1-SNAPSHOT",
  "com.typesafe.play" %% "play-mailer" % "6.0.1",
  "com.typesafe.play" %% "play-mailer-guice" % "6.0.1"
)


unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

