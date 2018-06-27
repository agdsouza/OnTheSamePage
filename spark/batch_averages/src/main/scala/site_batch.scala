import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import com.datastax.spark.connector._
import java.sql.Timestamp
import java.text.SimpleDateFormat
import scala.io.Source
//import com.typesafe.config.ConfigFactory
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
   val interval_divisor = 6

   // function to convert a timestamp to a time slot
   def convert_to_min(timestamp: String, interval: Int): String = {
       val minint = timestamp.slice(15,17).toInt/interval*interval
       timestamp.take(15) + f"${minint}%02d" + "00"
   }

   //def get_interval_start(starttime: Timestamp): String = {
   //    val df = new SimpleDateFormat("yyyy-MM-dd")
   //    val date = df.format(starttime)
   //    
   //}

//   def convert_to_timestamp(datetime: String): Timestamp = {
//       val dateFormat = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss")
//       val parsedDate = dateFormat.format(datetime)
//       val timestamp = new Timestamp(parsedDate.getTime)
//       return timestamp
//   }

   // read in the data from HDFS
   val raw_data = sc.cassandraTable("site_log", "raw_data")
   .select("url", "starttime")
   .where("starttime > ?", "2018-06-25 14:24:58")
   .keyBy(row => row.getString("url"))
   .map { case(key, value) => (key, 1) }
   .reduceByKey(_+_)
   .collect.foreach(println)

//   import org.apache.spark.sql.cassandra._
//   val df = spark
//   .read
//   .format("org.apache.spark.sql.cassandra")
//   .options(Map( "table" -> "raw_data", "keyspace" -> "site_log" ))
//   .load()
//   tableDf.show

   // map each record into a tuple consisting of (time, price, volume)
   //val ticks = file.map(line => {
   //                     val record = line.split(" ")
   //                    (record(0), record(1), record(2), record(3))
   //                             })

   // apply the time conversion to the time portion of each tuple and persist it memory for later use
   //val ticks_min30 = ticks.map(record => (record._1,
// 	 	       			  convert_to_min((record._2 + " " + record._3), 1),
//                                          record._4)).persist

   // compute the average price for each 30 minute period
   //val price_min30 = ticks_min30.map(record => ((record._1, record._2), 1))
   //                             .reduceByKey(_+_)

   //val ip_min = price_min30.map(record => (record._1._1, record._2)).persist

   // compute the total volume for each 30 minute period
   //val vol_min30 = ip_min.map(record => (record._1, (record._2, 1)))
   //                           .reduceByKey( (x, y) => (x._1 + y._1,
// 					      	     	       x._2 + y._2) )
							       //           .map(record => (record._1,
							       //				      record._2._1/record._2._2) )
							       					      				    

   // join the two RDDs into a new RDD containing tuples of (30 minute time periods, average price, total volume)
   //val price_vol_min30 = price_min30.join(vol_min30)
   //                                 .sortByKey()
   //                                 .map(record => (record._1,
   //                                                 record._2._1,
   //                                                 record._2._2))

   // save the data back into HDFS
   //vol_min30.saveAsTextFile("s3n://sessionizationbucket/site_output_scala3/")
   //ticks.bulkSaveToCassandra("playground", "website")
   //ticks_datetime.saveToCassandra("playground", "website", SomeColumns("id", "url"))
 }
}
