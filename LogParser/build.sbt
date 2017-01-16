name := "LogParser"

version := "1.0"

scalaVersion := "2.10.5"
//2.11.8

//Libraries needed
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.6.0",
  "org.apache.spark" %% "spark-sql" % "1.6.0",
  "org.slf4j" % "slf4j-simple" % "1.7.5",
  "org.clapper" %% "grizzled-slf4j" % "1.0.2"
)


// https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk
libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.3.11"

libraryDependencies += "com.databricks" % "spark-csv_2.10" % "1.5.0"

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql_2.10
//libraryDependencies += "org.apache.spark" % "spark-sql_2.10" % "1.6.0"

