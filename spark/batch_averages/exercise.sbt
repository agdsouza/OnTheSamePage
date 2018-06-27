name := "site_batch"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
"org.apache.spark" %% "spark-core" % "2.2.1" % "provided"
)

resolvers += "Spark Packages Repo" at "https://dl.bintray.com/spark-packages/maven"
libraryDependencies += "org.apache.spark" % "spark-sql_2.11" % "2.1.0"
libraryDependencies += "com.typesafe" % "config" % "1.3.0"
libraryDependencies += "com.datastax.spark" %% "spark-cassandra-connector" % "2.3.0"