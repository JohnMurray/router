import sbt._
import Keys._


object Resolvers {

   val sprayResolver = "spray repo" at "http://repo.spray.io"
}

object Dependencies {

   val akkaV = "2.3.0"
   val sprayV = "1.3.1"

   val appDependencies = Seq(
      "com.typesafe.akka" %% "akka-actor"   % akkaV,
      "com.typesafe.akka" %% "akka-testkit" % akkaV     % "test",
      "io.spray"          %  "spray-can"    % sprayV,
      "io.spray"          %% "spray-json"   % "1.2.5",
      "org.specs2"        %% "specs2"       % "2.3.10"  % "test"
   )
}

object BuildSettings {

   val buildOrganization = "johnmurray.io"
   val appName = "router"
   val buildVersion = "0.0.1-SNAPSHOT"
   val buildScalaVersion = "2.10.3"
   val buildScalaOptions = Seq("-unchecked", "-deprecation", "-encoding", "utf8")

   import Resolvers._
   import Dependencies._

   val buildSettings = Defaults.defaultSettings ++ Seq(
      organization        := buildOrganization,
      version             := buildVersion,
      scalaVersion        := buildScalaVersion,
      resolvers           += sprayResolver,
      libraryDependencies := appDependencies,
      scalacOptions       := buildScalaOptions
   )
}

object ApplicationBuild extends Build {

   import BuildSettings._

   lazy val main = Project(
      appName,
      file("."),
      settings = buildSettings)
}
