import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql.{SaveMode, DataFrame}
import org.apache.spark.sql._
/**
  * Created by nandakishore on 9/7/2016.
  */
object logparser {

  case class LogInfo(originatingIp: String, clientIdentity: String, userId: String,timeStamp:java.sql.Date, zone: String, requestType: String, requestPage: String, httpProtocolVersion: String, responseCode: String, responseSize: String, referrer: String, userAgent: String)

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("Log Parser").setMaster("yarn-cluster")
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    import sqlContext.implicits._

    sc.hadoopConfiguration.set("fs.s3n.awsAccessKeyId", args(2))
    sc.hadoopConfiguration.set("fs.s3n.awsSecretAccessKey", args(3))

    //Source data from S3
    val logData = sc.textFile(args(0))

    val pattern = """^([\d.]+) (\S+) (\S+) \[([\w:/]+\s[+\-]\d{4})\] \"([^/]+)\/(.+) (.+)\" (\d{3}) (\d+) \"([^\"]+)\" \"([^\"]+)\".*""".r

    val rawRegexParseRDD = logData.map(x => pattern.findFirstMatchIn(x)).flatMap(x => x).map(x => LogInfo(x.group(1), x.group(2), x.group(3), new java.sql.Date(new java.text.SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z").parse(x.group(4)).getTime()), x.group(4).substring(23, 26), x.group(5), x.group(6), x.group(7), x.group(8), x.group(9), x.group(10), x.group(11).replace(",",""))).toDF()

    rawRegexParseRDD.registerTempTable("logDataTable")

    val finalDF = sqlContext.sql(" SELECT originatingIp,responseCode,requestType,requestPage,userAgent from logDataTable where requestPage != '' and originatingIp IS NOT NULL and userAgent IS NOT NULL and userId IS NOT NULL")

    //Save output to S3
    finalDF.rdd.saveAsTextFile(args(1))

  }
}
