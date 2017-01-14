name := "ClickstreamKafkaProducer"

version := "1.0"

scalaVersion := "2.10.5"

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" % "0.8.2.1",
  "org.apache.kafka" % "kafka-clients" % "0.8.2.1"
)