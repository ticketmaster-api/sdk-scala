import java.io.File

import Dependencies._
import sbt.Keys._
import sbt.{Build, ConflictManager, Path, Resolver, _}
import sbtrelease.ReleasePlugin.autoImport._
import tut.Plugin._

object SdkBuild extends Build {
  lazy val commonSettings = Seq(
    organization := "com.ticketmaster.api",
    scalaVersion := "2.11.7",
    scalacOptions ++= Seq(
      "-feature",
      "-Xfatal-warnings",
      "-language:postfixOps"),
    conflictManager := ConflictManager.strict,
    dependencyOverrides ++= depOverrides,
    publishTo := Some(Resolver.file("file", new File(Path.userHome.absolutePath + "/.m2/repository")))
  )

  lazy val root = (project in file("."))
    .settings(commonSettings: _*)
    .settings(
      releasePublishArtifactsAction := {},
      tutSettings,
      tut := {
        val tutted = tut.value
        val maybeReadme = tutted.find(_._2.equals("README.md")).map(f => f._1)
        if (maybeReadme.isDefined) {
          IO.copyFile(maybeReadme.get, baseDirectory.value / "README.md")
        }
        tutted
      }
    )
    .dependsOn(discovery)
    .aggregate(discovery)

  lazy val core = project
    .settings(commonSettings: _*)
    .settings(
      name := """core-scala""",
      libraryDependencies ++= coreDeps)

  lazy val discovery = project
    .settings(commonSettings: _*)
    .settings(
      name := """discovery-scala""",
      libraryDependencies ++= discoveryDeps,
      sourceGenerators in Compile += Def.task {
        val file = (sourceManaged in Compile).value / "build" / "Info.scala"
        IO.write(file,
          s"""
             |package build
             |
             |object Info {
             |  val version = "${version.value}"
             |}
          """.stripMargin)
        Seq(file)
      }.taskValue)
    .dependsOn(core)
    .aggregate(core)
}
