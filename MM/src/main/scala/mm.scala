/**
  * Created by nandakishore on 7/16/2016.
  */
import java.sql.{ResultSet, DriverManager}
import java.util.Properties

import org.apache.spark.{Logging, SparkContext, SparkConf}
import org.apache.spark.sql._
import scala.collection.immutable.{Nil}
import scala.util.control.Breaks._
import org.apache.spark.sql.types.{StructType}
import com.typesafe.config.{ConfigObject, ConfigFactory}
import scala.io.Source

object mm extends App with Logging {

  val conf = new SparkConf().setAppName("MM_Project").setMaster("yarn-cluster")
  val sc = new SparkContext(conf)

  val sqlContext = new org.apache.spark.sql.SQLContext(sc)

  Class.forName( "com.mysql.jdbc.Driver" )

  var userMM = ""
  var PasswordMM = ""
  var tablesFileMM = ""
  var nullStringMM = ""
  var nonNullStringMM = ""
  var fetchSizeMM = "0"

  //Fetch configuration file passed as an argument
  var configFile = scala.io.Source.fromFile(args(2))
  for(config <- configFile.getLines())
  {
    config.split("=")(0) match {

      case "username" => userMM = config.split("=")(1)
      case "password" => PasswordMM = config.split("=")(1)
      case "nullString" => nullStringMM = config.split("=")(1)
      case "nonNullString" => nonNullStringMM = config.split("=")(1)
      case "fetchSize" => fetchSizeMM = config.split("=")(1)
      case "tablePath" => tablesFileMM = config.split("=")(1)
      case _ => "novalue"
    }
  }

  //Setting up the user and password for MySql connection
  val connectionProperties : Properties = new Properties()
  connectionProperties.put("user", userMM)
  connectionProperties.put("password", PasswordMM)


  //Get the minimum and maximum value of a table for a particular POD
  def getMinMax(port :Int, table: String) : String = {

    var keyColumn = ""
    var keyDataType = ""
    var keyConstraint = ""


    val urlInformationSchema = "jdbc:mysql://localhost:" + port.toString + "/information_schema"
    // logger.info(connectionProperties.get(user) + "user  --- pwd" + connectionProperties.get(password) )
    val conInformationSchema = DriverManager.getConnection(urlInformationSchema, connectionProperties)
    // logger.info("connection was possible")
    val stInformationSchema = conInformationSchema.createStatement()

    val queryPrimarykeyType = "select column_name,column_type,column_key from columns where table_name = '"+ table + "'" +
      " and column_key = 'PRI' "

    val rsPriKey = stInformationSchema.executeQuery(queryPrimarykeyType)
    var count = 0

    breakable{
      while(rsPriKey.next()) {

        keyColumn = rsPriKey.getString(1)
        keyDataType = rsPriKey.getString(2)
        keyConstraint = rsPriKey.getString(3)

        count += 1
        if (count == 1) break

      }
    }


    conInformationSchema.close()

    if((!keyColumn.isEmpty) & keyDataType.toLowerCase.startsWith("int"))
    {
      val urlEMA = "jdbc:mysql://localhost:" + port.toString + "/ema"
      val con = DriverManager.getConnection(urlEMA, connectionProperties)
      val st = con.createStatement()

      val query = "select min(" + keyColumn + "),max(" + keyColumn + ") from " + table

      val rsMinMax: ResultSet = st.executeQuery(query)
      var min_value = ""
      var max_value = ""

      while (rsMinMax.next()) {
        min_value = rsMinMax.getString(1)
        if (min_value == "null" || min_value == null || min_value == "") min_value = "0"

        max_value = rsMinMax.getString(2)
        if (max_value == "null" || max_value == null || max_value == "") max_value = "0"
      }


      val table_size_query = "select round(((data_length + index_length) / 1024 / 1024), 2) as tbl_size from information_schema.TABLES" +
        " where table_name  = '" + table + "'"

      val rs_size: ResultSet = st.executeQuery(table_size_query)
      var num_partitions = 0
      var size_pod = 0.0
      while (rs_size.next()) {
        size_pod = rs_size.getDouble(1)

        if(size_pod < 50.0) num_partitions = 2
        else if(size_pod >= 50 && size_pod < 200) num_partitions = 10
        else if(size_pod >= 200 && size_pod < 1000) num_partitions = 15
        else if(size_pod >= 1000 && size_pod < 5000) num_partitions = 100
        else if(size_pod >= 5000 && size_pod < 15000) num_partitions = 300
        else if(size_pod >= 15000) num_partitions = 1700
      }

      //Close Connection
      con.close()

      min_value + " " + max_value + " " + num_partitions + " " + keyColumn

    }
    else
    {
      ""
    }
  }

  //Get the tables list to be imported
  var tables = Source.fromFile(tablesFileMM).getLines()

  //Looping th' the tables

  for(table <- tables) {

    val schema = StructType(Nil)

    var df1 = sqlContext.createDataFrame(sc.emptyRDD[Row],schema).toDF()

    val url = "jdbc:mysql://localhost:"+args(0)+"/ema"

    /*
    getMinMax will get the minimum and maximum value of a primary key column, if it exists that is used to parallelize the
    Spark ingestion process. If the function has 4 values in its returned value then its parallelized otherwise the
    whole data is just dumped onto HDFS using single system core.
    */
    val stat1 = getMinMax(args(0).toInt, table)
    if(stat1.split(" ").length == 4) {
      df1 = sqlContext.read.format("jdbc").option("url", url)
        .option("driver", "com.mysql.jdbc.Driver").option("dbtable", table).option("user", userMM)
        .option("password", PasswordMM).option("partitionColumn", stat1.split(" ")(3)).option("lowerBound", stat1.split(" ")(0)).option("upperBound", stat1.split(" ")(1))
        .option("numPartitions", stat1.split(" ")(2)).option("fetchSize", fetchSizeMM).load().na.fill(nullStringMM) //.na.fill(nonNullString)
    }
    else{
      df1 = sqlContext.read.format("jdbc").option("url", url)
        .option("driver", "com.mysql.jdbc.Driver").option("dbtable", table).option("user", userMM)
        .option("password", PasswordMM).load().na.fill(nullStringMM) //.na.fill(nonNullString) //.withColumn("pod",lit(portPod.get(ports(0)).get))
    }

      //Output directory on HDFS
      val output_path = "/user/hadoop/MMFiles/" + table + "/" + args(1).toString + "/"

      df1.write.mode(SaveMode.Append).save(output_path)

  }
}

