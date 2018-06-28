import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import com.datastax.spark.connector._
import java.sql.Timestamp
import java.text.SimpleDateFormat
import scala.io.Source
import java.util.Date
import com.datastax.spark.connector._
import org.apache.spark.sql.cassandra._
import org.apache.spark.sql.functions._

object site_batch {

 case class WebLog(url: String, starttime: Timestamp)

 def main(args: Array[String]) {

   // setup the Spark Context named sc
   val conf = new SparkConf().setAppName("WebData")
   val sc = new SparkContext(conf)

   // what to divide the totals of the MapReduce counts (default to 12, to represent 5 second intervals)
   val interval_divisor = 12

   // read in the data from HDFS
   val raw_data = sc.cassandraTable("site_log", "raw_data")
   .select("url", "starttime")
   .where("starttime > ?", "2018-06-25 14:24:58")
   .keyBy(row => row.getString("url"))
   .map { case(key, value) => (key, 1) }
   .reduceByKey(_+_)
   .collect.foreach(println)

 }
}
