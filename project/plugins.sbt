resolvers ++= Seq(
  "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "scoverage-bintray" at "https://dl.bintray.com/sksamuel/sbt-plugins/"
)

Seq(
  "org.scalastyle" %% "scalastyle-sbt-plugin" % "0.7.0",
  "org.scoverage" % "sbt-scoverage" % "1.3.1",
  "net.virtual-void" % "sbt-dependency-graph" % "0.8.2",
  "com.timushev.sbt" % "sbt-updates" % "0.1.9",
  "com.github.gseitz" % "sbt-release" % "1.0.3"
).map(addSbtPlugin)
