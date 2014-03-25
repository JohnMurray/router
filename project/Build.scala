import sbt._
import Keys._


object BuildSettings {
  val buildOrganization = "johnmurray.io"
  val appName           = "router"
  val buildVersion      = "0.0.1-SNAPSHOT"
  val buildScalaVersion = "2.10.3"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion
  )
}

object Resolvers {

}

object Dependencies {

}

object ApplicationBuild extends Build {
  import BuildSettings._
  import Resolvers._
  import Dependencies._

  lazy val main = Project(
    "router",
    file("."),
    settings = buildSettings)
}
