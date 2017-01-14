name := "ClickstreamSparkstreaming"

version := "1.0"

scalaVersion := "2.10.5"
//val scalaToolsVersion = "2.11"
/*
<scala.version>2.11.8</scala.version>


  <spark.version>2.0.0</spark.version>
  <log4j.version>1.2.17</log4j.version>
  <jackson.version>2.4.4</jackson.version>
  <commons-codec.version>1.2</commons-codec.version>
  <commons-lang.version>2.6</commons-lang.version>
  <joda-time.version>2.3</joda-time.version>
  <maven.compiler.source>1.7</maven.compiler.source>
  <maven.compiler.target>1.7</maven.compiler.target>
*/

libraryDependencies ++= Seq(
/*  "org.scala-lang" %% "scala-compiler" % "2.11.8",
  "org.scala-lang" %% "scala-library" % "2.11.8",
  "org.apache.spark" %% "spark-core_2.11" % "2.0.0",
  "org.apache.spark" %% "spark-streaming_2.11" % "2.0.0",
*/
 // "org.scala-lang" %% "scala-compiler" % "2.10.5",
  //"org.scala-lang" %% "scala-library" % "2.10.5",
  "org.apache.spark" %% "spark-core" % "1.6.2",
  "org.apache.spark" %% "spark-sql" % "1.6.2",
  "org.apache.spark" % "spark-streaming-kafka_2.10" % "1.6.2",
  "org.apache.spark" %% "spark-streaming" % "1.6.2",
  "org.scala-lang" % "scala-reflect" % "2.10.5"
  //"org.slf4j" % "slf4j-simple" % "1.7.5",
  //"org.clapper" %% "grizzled-slf4j" % "1.0.2"

)

// https://mvnrepository.com/artifact/org.apache.spark/spark-hive_2.10
libraryDependencies += "org.apache.spark" % "spark-hive_2.10" % "1.6.2"


// https://mvnrepository.com/artifact/org.json4s/json4s-native_2.11
//libraryDependencies += "org.json4s" % "json4s-native_2.10" % "3.4.2"
