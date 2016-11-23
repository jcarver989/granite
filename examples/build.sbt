import sbt._
import Keys._

enablePlugins(ScalaJSPlugin)
name := "Bedrock Example"
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


libraryDependencies += "com.bedrock" %%% "bedrock" % "0.0.1"

scalaJSUseRhino in Global := false
requiresDOM := true
