name := """discovery-scala"""

organization := "com.ticketmaster.api"

scalaVersion := "2.11.7"

scalacOptions ++= Seq(
  "-feature",
  "-Xfatal-warnings")

conflictManager := ConflictManager.strict

libraryDependencies ++= Seq(
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
  "io.argonaut" %% "argonaut" % "6.1",
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % "test"
)

dependencyOverrides ++= Set(
  "org.scala-lang" % "scala-library" % "2.11.7",
  "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.2",
  "org.scala-lang.modules" % "scala-parser-combinators_2.11" % "1.0.4",
  "org.scala-lang" % "scala-reflect" % "2.11.7"
)

tutSettings

// Change README.md in src/main/tut, run 'sbt tut' to produce new README.md with valid code
tut := {
  val tutted = tut.value
  val maybeReadme = tutted.find(_._2.equals("README.md")).map(f => f._1)
  if(maybeReadme.isDefined) { IO.copyFile(maybeReadme.get, baseDirectory.value / "README.md")}
  tutted
}

//todo change to internal nexus
publishTo := Some(Resolver.file("file", new File(Path.userHome.absolutePath + "/.m2/repository")))

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
}.taskValue
