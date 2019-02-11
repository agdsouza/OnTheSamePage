name := "site_data"

version := "1.0"

scalaVersion := "2.11.12"

val sparkVersion =  "2.4.0"

libraryDependencies ++= Seq(
"org.apache.spark" %% "spark-core" % sparkVersion % "provided",
"org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
"org.apache.spark" %% "spark-streaming" % sparkVersion % "provided",
"org.apache.spark" %% "spark-streaming-kafka-0-8" % sparkVersion
)
libraryDependencies += "org.apache.hadoop" % "hadoop-aws" % "3.2.0"
// libraryDependencies += "datastax" % "spark-cassandra-connector" % "2.3.0-s_2.11"
libraryDependencies += "com.datastax.spark" %% "spark-cassandra-connector" % sparkVersion
// libraryDependencies += "com.datastax.spark" %% "spark-cassandra-connector" % "2.4.0-s_2.11"
libraryDependencies += "com.typesafe" % "config" % "1.3.2"

mergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith("manifest.mf")          => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*\\.sf$")      => MergeStrategy.discard
  case "log4j.properties"                                  => MergeStrategy.discard
  case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
  case "reference.conf"                                    => MergeStrategy.concat
  case _                                                   => MergeStrategy.first
}
