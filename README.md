# OnTheSamePage
*Documentation is a work in progress*

My Insight Data Engineering project for the Summer 2018 session in New York. "On the Same Page" is an application that uses a data pipeline to monitor website pages in real time

# Motivation
In web development, it is important to understand how users interact with pages in their websites to compile analytics, monitor any abnormal-or even dangerous-changes in web traffic, and track pages that users are most engaged with. Moreover, page-by-page monitoring can help news and video websites understand the articles or videos that attract the most users, allow data scientists to analyze popular topics, and advertisers determine pages that make the most money.

# Pipeline
![alt text](https://github.com/agdsouza/OnTheSamePage/img/Screen\Shot\2018-06-27\at\5.21.57\PM.png "ETL Pipeline")
On the Same Page is an application that displays this page-by-page monitoring in real time. Using Kafka to ingest messages, Spark Streaming to calculate user counts in 5 second intervals along with Spark batch jobs to calculate average page counts every minute as a baseline, and Cassandra to store the processed data to be queried, the data is then rendered in Dash to show real-time updates to user visits every 5 seconds.

*picture of pipeline to be added once complete*

# Getting Started
*to be added later*
