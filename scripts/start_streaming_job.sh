#!/bin/bash

IP_ADDR="$(hostname)"

echo "Starting Spark Streaming Job"
cd ~/spark/streaming_counts
spark-submit --class site_data --master spark://$IP_ADDR --jars target/scala-2.11/site_data-assembly-1.0.jar target/scala-2.11/site_data-assembly-1.0.jar
echo "Spark Job Completed"