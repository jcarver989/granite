import sbt._
import Keys._

enablePlugins(ScalaJSPlugin)
name := "Bedrock"
scalaVersion := "2.11.8" 
organization := "com.bedrock"
version := "0.0.1"
scalacOptions := Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-Xfatal-warnings",
    "-Xlint",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Xfuture")


libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.0"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.0"
libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.0" % "test"
libraryDependencies += "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided"
libraryDependencies += "com.lihaoyi" %%% "scalatags" % "0.6.1"
libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.4.3"

jsDependencies += ProvidedJS / "morphdom.js"

scalaJSUseRhino in Global := false
requiresDOM := true
