name := "WestField"

version := "1.0"

scalaVersion := "2.10.5"
  //"2.11.8"


libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.6.0",
  "org.apache.spark" %% "spark-sql" % "1.6.0",
  "org.slf4j" % "slf4j-simple" % "1.7.5"
)

// https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk
libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.3.11"


