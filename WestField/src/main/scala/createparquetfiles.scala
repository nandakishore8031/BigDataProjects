import org.apache.spark.{SparkContext, SparkConf}
import sun.util.logging.resources.logging
import scala.io._
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
  * Created by nandakishore on 9/23/2016.
  */
object createparquetfiles{

  val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("WestField Project").setMaster("yarn-cluster")
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    import sqlContext.implicits._

    var schema = ""
    val configFile = Source.fromFile(args(1))
    for(config <- configFile.getLines())
      {
        config.split("=")(0) match {

          case "schema" => schema = config.split("=")(1)
          case _ => "novalue"
        }
      }
    logger.info("schema noted is : " + schema)

    //Input S3 Path
    val inputPath = "s3a://westfield-data-retailnext-dev/firehose/"
    val datePath = args(0) + "/"

    //Build main Input path with date
    val s3Path = new StringBuilder
    s3Path.append(inputPath)
    s3Path.append(datePath)

    //Get Json Input files
    val jsonfiles = sqlContext.jsonFile(s3Path.toString())

    //Get the Json first level columns
    val fields = jsonfiles.columns

    var firstLevelFields = ""
    for( field <- fields){

      val k = field.replace(" ","")

      if(firstLevelFields.equals(""))
        firstLevelFields += k
      else {
        firstLevelFields += ".*,"
        firstLevelFields += k
      }
    }

    firstLevelFields += ".*"

    //registerTempTable
    jsonfiles.registerTempTable("jsontable")

    val sqlQuery = "select " + firstLevelFields  + " from jsontable"
    //logger.info("sql query is : " + sqlQuery)
    val allFieldsDF = sqlContext.sql(sqlQuery)


    allFieldsDF.registerTempTable("westfieldtable")

    val allColumnsArr = allFieldsDF.columns

    var allFields = ""

    //Replace spaces for fields names
    for(col <- allColumnsArr){

      val newCol = col.replace(" ","")

      if(allFields.equals(""))
        allFields += "`" + col + "` as " + newCol
      else {
        allFields += ","
        allFields += "`" + col + "` as " + newCol
      }
    }

    val finalDF = sqlContext.sql("select " + allFields + " from westfieldtable")

    val outputPath = new StringBuilder
    outputPath.append("/user/wfdev/westfield-data-retailnext-dev/")
    outputPath.append(datePath)

    //Saves data in parquet format
    finalDF.saveAsParquetFile(outputPath.toString())

  }
}
