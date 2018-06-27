name := "site_data"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
"org.apache.spark" %% "spark-core" % "2.2.1" % "provided",
"org.apache.spark" %% "spark-sql" % "2.2.1" % "provided",
"org.apache.spark" %% "spark-streaming" % "2.2.1" % "provided",
"org.apache.spark" %% "spark-streaming-kafka-0-8" % "2.2.1"
)
libraryDependencies += "datastax" % "spark-cassandra-connector" % "2.3.0-s_2.11"
libraryDependencies += "com.typesafe" % "config" % "1.3.2"

mergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf")          => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*\\.sf$")      => MergeStrategy.discard
  case "log4j.properties"                                  => MergeStrategy.discard
  case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
  case "reference.conf"                                    => MergeStrategy.concat
  case _                                                   => MergeStrategy.first
}
