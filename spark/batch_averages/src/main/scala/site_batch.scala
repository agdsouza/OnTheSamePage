import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import com.datastax.spark.connector._
import com.datastax.spark.connector.cql._
import java.sql.Timestamp
import java.text.SimpleDateFormat
import scala.io.Source
import java.util.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}
import com.datastax.spark.connector._
import org.apache.spark.sql.cassandra._
import org.apache.spark.sql.functions._

object site_batch {

 def main(args: Array[String]) {

   // setup the Spark Context named sc
   val conf = new SparkConf().setAppName("WebDataExample")
   val sc = new SparkContext(conf)
   val connectorToClusterOne = CassandraConnector(sc.getConf.set("spark.cassandra.connection.host", "52.73.46.190"))

   // constant: divide the number of visitors by IntervalDivisor to obtain the average; alter this in case of delays
   val IntervalDivisor = 6

    // function that gets the current timestamp to make queries later on
    def getCurrentdateTimeStamp: String ={
      val today:java.util.Date = Calendar.getInstance.getTime
      val timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      val now:String = timeFormat.format(today)
      return now
    }

    // function that adds "minutes" amount of minutes to the inputted timestamp
    def dateAddMinute(date: String, minutes: Int, inputFormat: String, outputFormat: String) : String = {
      val dateAux = Calendar.getInstance()
      dateAux.add(Calendar.MINUTE, minutes)
      return new SimpleDateFormat(outputFormat).format(dateAux.getTime())
    }

   // read in the data from HDFS
   val now = dateAddMinute(getCurrentdateTimeStamp, -1, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss")
   val prev_time = dateAddMinute(getCurrentdateTimeStamp, -2, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss")
   val rddFromClusterOne = {
       // Sets connectorToClusterOne as default connection for everything in this code block
       implicit val c = connectorToClusterOne
       sc.cassandraTable("site_log", "raw_data")
           .select("url", "starttime")
           .where("starttime > ?", prev_time)
           .where("starttime < ?", now)
           .keyBy(row => row.getString("url"))
           .map { case(key, value) => (key, 1) }
           .reduceByKey(_+_)
           .map {case(key, value) => (key, getCurrentdateTimeStamp, value/IntervalDivisor) }
           .saveToCassandra("site_log", "page_averages", SomeColumns("url", "time", "average"))
       }
   }
}

