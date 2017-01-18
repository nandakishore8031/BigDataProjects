name := "MM"

version := "1.0"

scalaVersion := "2.10.5"
//2.10.5

//Libraries needed
libraryDependencies ++= Seq(
        "org.apache.spark" %% "spark-core" % "1.6.0",
        "org.apache.spark" %% "spark-sql" % "1.6.0",
        "org.slf4j" % "slf4j-simple" % "1.7.5",
        "org.clapper" %% "grizzled-slf4j" % "1.0.2")

// https://mvnrepository.com/artifact/org.apache.hive/hive-jdbc
libraryDependencies += "org.apache.hive" % "hive-jdbc" % "0.13.1"

// https://mvnrepository.com/artifact/org.apache.spark/spark-hive_2.10
libraryDependencies += "org.apache.spark" % "spark-hive_2.10" % "1.6.1"

//For configuration file
libraryDependencies += "com.typesafe" % "config" % "1.3.0"

// https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-aws
//libraryDependencies += "org.apache.hadoop" % "hadoop-aws" % "2.7.1"
