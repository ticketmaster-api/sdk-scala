import Dependencies._
import sbt.Keys._
import sbt.{Build, ConflictManager, _}
import sbtrelease.ReleasePlugin.autoImport._
import tut.Plugin._
import bintray.BintrayKeys._

object SdkBuild extends Build {
  lazy val commonSettings = Seq(
    organization := "com.ticketmaster.api",
    scalaVersion := "2.11.7",
    scalacOptions ++= Seq(
      "-feature",
      "-Xfatal-warnings",
      "-language:postfixOps",
      "-language:implicitConversions"),
    conflictManager := ConflictManager.strict,
    dependencyOverrides ++= depOverrides
  )

  lazy val publishSettings = Seq(
    bintrayOrganization := Some("ticketmaster-api"),
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
    bintrayPackageLabels := Seq("ticketmaster", "api", "scala"),
    resolvers += Resolver.url("ticketmaster api ivy resolver", url("http://dl.bintray.com/ticketmaster-api/maven"))(Resolver.ivyStylePatterns)
  )

  lazy val unpublishSettings = Seq(
    releasePublishArtifactsAction := {},
    publish := {},
    bintrayEnsureBintrayPackageExists := {},
    bintrayUnpublish := {}
  )

  lazy val root = (project in file("."))
    .settings(commonSettings: _*)
    .settings(unpublishSettings: _*)
    .settings(
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
    .dependsOn(discovery, commerce)
    .aggregate(discovery, commerce)

  lazy val core = project
    .settings(commonSettings: _*)
    .settings(publishSettings: _*)
    .settings(
      name := """core-scala""",
      libraryDependencies ++= coreDeps,
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

  lazy val discovery = project
    .settings(commonSettings: _*)
    .settings(publishSettings: _*)
    .settings(
      name := """discovery-scala""",
      libraryDependencies ++= discoveryDeps)
    .dependsOn(core)
    .aggregate(core)

  lazy val commerce = project
    .settings(commonSettings: _*)
    .settings(publishSettings: _*)
    .settings(
      name := """commerce-scala""",
      libraryDependencies ++= commerceDeps
    )
    .dependsOn(core)
    .aggregate(core)
}
