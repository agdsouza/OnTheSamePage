# On The Same Page
My Insight Data Engineering project for the NY Summer 2018 session. "On the Same Page" is an application that uses a data pipeline to monitor website pages in real time.

# Motivation
In web development, it is important to understand how users interact with pages in their websites to compile analytics, monitor any abnormal-or even dangerous-changes in web traffic, and track pages that users are most engaged with. Moreover, page-by-page monitoring can help news and video websites understand the articles or videos that attract the most users, allow data scientists to analyze popular topics, and advertisers determine pages that make the most money.

# Pipeline
![alt text](img/pipeline.png)
"On the Same Page" runs a pipeline on the AWS cloud, using the following cluster configurations:

* four m4.large EC2 instances for Kafka
* four m4.large EC2 instances for the Spark Streaming job and Cassandra 
* three m4.large EC2 instances for the Spark batch job
* one t2.micro EC2 instance to run the Dash front-end application

Using Kafka to ingest messages, Spark Streaming to calculate user counts in 5 second intervals along with Spark batch jobs to calculate average page counts every minute as a baseline, and Cassandra to store the processed data to be queried, the data is then rendered in Dash to show real-time updates to user visits every 5 seconds.

# Getting Started
See the [wiki](https://github.com/agdsouza/OnTheSamePage/wiki/Getting-Started) page for instructions on getting started.
