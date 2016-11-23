import sbt._
import Keys._

enablePlugins(ScalaJSPlugin)
name := "Granite Example"
scalaVersion := "2.11.8" 
organization := "com.granite"
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


libraryDependencies += "com.granite" %%% "granite" % "0.0.1"

scalaJSUseRhino in Global := false
requiresDOM := true
