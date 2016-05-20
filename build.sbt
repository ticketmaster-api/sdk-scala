import sbt.Keys._


lazy val commonSettings = Seq(
  organization := "com.ticketmaster.api",
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq(
    "-feature",
    "-Xfatal-warnings",
    "-language:postfixOps"),
  conflictManager := ConflictManager.strict,
  dependencyOverrides ++= Set(
    "org.scala-lang" % "scala-library" % "2.11.7",
    "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.2",
    "org.scala-lang.modules" % "scala-parser-combinators_2.11" % "1.0.4",
    "org.scala-lang" % "scala-reflect" % "2.11.7"
  ),
  publishTo := Some(Resolver.file("file", new File(Path.userHome.absolutePath + "/.m2/repository")))
)

lazy val root = (project in file("."))
  .dependsOn(discovery)
  .aggregate(discovery)
  .settings(commonSettings: _*)
  .settings(releasePublishArtifactsAction := {})

lazy val discovery = project
  .dependsOn(core)
  .aggregate(core)
  .settings(commonSettings: _*)

lazy val core = project
  .settings(commonSettings: _*)

tutSettings

tut := {
  val tutted = tut.value
  val maybeReadme = tutted.find(_._2.equals("README.md")).map(f => f._1)
  if (maybeReadme.isDefined) {
    IO.copyFile(maybeReadme.get, baseDirectory.value / "README.md")
  }
  tutted
}