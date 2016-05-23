import sbt._

object Dependencies {
  val argonautShapeless = "com.github.alexarchambault" %% "argonaut-shapeless_6.1" % "1.0.0-M1"
  val dispatch = "net.databinder.dispatch" %% "dispatch-core" % "0.11.2"
  val slf4j = "org.slf4j" % "slf4j-api" % "1.7.5"
  val scalatest = "org.scalatest" %% "scalatest" % "2.2.4"
  val scalamock = "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2"

  val depOverrides = Set(
    "org.scala-lang" % "scala-library" % "2.11.7",
    "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.2",
    "org.scala-lang.modules" % "scala-parser-combinators_2.11" % "1.0.4",
    "org.scala-lang" % "scala-reflect" % "2.11.7"
  )

  val coreDeps = Seq(
    dispatch
  )

  val discoveryDeps = Seq(
    argonautShapeless,
    slf4j,
    scalatest % "test",
    scalamock % "test"
  )
}