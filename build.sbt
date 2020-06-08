name := """filloa"""
organization := "com.filloa"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

// scalaVersion := "2.13.2"
scalaVersion := "2.12.11"

libraryDependencies += guice
libraryDependencies += specs2 % Test
libraryDependencies += "com.pauldijou" %% "jwt-play-json" % "4.3.0"

// libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.filloa.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.filloa.binders._"