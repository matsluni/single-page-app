name := "Single Page App"

version := "1.0"

scalaVersion := "2.10.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "sonatype releases"  at "https://oss.sonatype.org/content/repositories/releases/",
  "sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "typesafe repo"      at "http://repo.typesafe.com/typesafe/releases/",
  "spray repo"         at "http://repo.spray.io/",
  "akka repo"          at "http://repo.akka.io/releases/"
)

libraryDependencies ++= {
  val camelVersion = "2.10.0"
  val sprayVersion = "1.2-M8"
  val akkaVersion = "2.2.0-RC1"
  Seq(
    "org.apache.camel"          %  "camel-core"            % camelVersion,
    "org.apache.camel"          %  "camel-http4"           % camelVersion,
    "io.spray"                  %  "spray-can"             % sprayVersion,
    "io.spray"                  %  "spray-routing"         % sprayVersion,
    "io.spray"                  %  "spray-testkit"         % sprayVersion,
    "com.typesafe.akka"         %% "akka-actor"            % akkaVersion,
    "com.typesafe.akka"         %% "akka-camel"            % akkaVersion,
    "com.typesafe.akka"         %% "akka-testkit"          % akkaVersion,
    "org.slf4j"                 %  "slf4j-api"             % "1.6.6",
    "org.slf4j"                 %  "slf4j-log4j12"         % "1.6.6",
    "log4j"                     %  "log4j"                 % "1.2.17",
    "io.spray"                  %% "spray-json"            % "1.2.5",
    "com.github.nscala-time"    %% "nscala-time"           % "0.6.0",
    "org.specs2"                %% "specs2"                % "1.14"             % "test",
    "org.scalatest"             %% "scalatest"             % "1.9.1"            % "test"
)}

seq(Revolver.settings: _*)
