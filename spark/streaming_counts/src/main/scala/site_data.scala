import kafka.serializer.StringDecoder

import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import com.datastax.spark.connector.streaming._
import com.datastax.spark.connector._
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}
import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import scala.concurrent.duration._

object PriceDataStreaming {
  def main(args: Array[String]) {

    val brokers = "ec2-18-204-77-15.compute-1.amazonaws.com:9092"
    val topics = "site_log"
    val topicsSet = topics.split(",").toSet
    val interval = 5

    // Create context with 2 second batch interval
    val sparkConf = new SparkConf().setAppName("price_data")
    val ssc = new StreamingContext(sparkConf, Seconds(interval))

    // Create direct kafka stream with brokers and topics
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers)
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topicsSet)
    ssc.checkpoint("s3n://sessionizationbucket/site_log_checkpoint/")

    // gets the current time stamp to put in the database
    def getCurrentdateTimeStamp: Timestamp ={
      val today:java.util.Date = Calendar.getInstance.getTime
      val timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      val now:String = timeFormat.format(today)
      val re = java.sql.Timestamp.valueOf(now)
      return re
    }

    // start processing the data
    val lines = messages.map(_._2)
    val ticksDF = lines.map( x => {
                              val tokens = x.split(";")
                              (tokens(2), tokens(1), tokens(3))}).persist()

    // write raw data into cassandra for batch processing
    val ticks_raw = ticksDF.map(record => (record._1, java.sql.Timestamp.valueOf(record._2), record._3))
    ticks_raw.saveToCassandra("site_log", "raw_data", SomeColumns("ip", "starttime", "url"))

    // get user count every second, then add to page_visits table
    val ticks_interval_page = ticksDF.map(record => ((record._3), 1))
                                     .reduceByKeyAndWindow((a:Int, b:Int) => a + b, Seconds(interval), Seconds(interval))
                                     .map(record => (record._1, getCurrentdateTimeStamp, interval, record._2))
    ticks_interval_page.saveToCassandra("site_log", "page_visit_table", SomeColumns("url", "interval_time", "duration", "visits"))

    // get unique user count every second, then add to page_visits_unique
    val ticks_interval_unique = ticksDF.window(Seconds(5), Seconds(5))
                                       .map(record => ((record._1,record._3), 1))
                                       .reduceByKey( (x, y) => (x) )
                                       .map(record => ((record._1._2), 1))
                                       .reduceByKey((a:Int, b:Int) => a + b)
                                       .map(record => (record._1, getCurrentdateTimeStamp, interval, record._2))
    ticks_interval_unique.saveToCassandra("site_log", "unique_page_visit_table", SomeColumns("url", "interval_time", "duration", "unique_visits"))

    // Start the computation
    ssc.start()
    ssc.awaitTermination()
  }
}
