name := """discovery-scala"""

libraryDependencies ++= Seq(
  "com.github.alexarchambault" %% "argonaut-shapeless_6.1" % "1.0.0-M1",
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % "test"
)

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