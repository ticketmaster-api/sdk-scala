resolvers ++= Seq(
  "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "scoverage-bintray" at "https://dl.bintray.com/sksamuel/sbt-plugins/"
)

Seq(
  "net.virtual-void" % "sbt-dependency-graph" % "0.8.2",
  "com.timushev.sbt" % "sbt-updates" % "0.1.9",
  "com.github.gseitz" % "sbt-release" % "1.0.3",
  "org.tpolecat" % "tut-plugin" % "0.4.2"
).map(addSbtPlugin)
