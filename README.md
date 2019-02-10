# Data Pipelines in K8S
My Insight DevOps Engineering project for the NY 2019A session. The pipeline I used for this project is from [agdsouza/OnTheSamePage](https://github.com/agdsouza/OnTheSamePage) that supports monitoring of website pages in real time.

A video demo of the pipeline operation in K8S can be found [here](https://youtu.be/Mec7F5CQqa8).

# Motivation
While running containerized stateless applications in Kubernetes has been proved very effective, deploying stateful components like Kafka, Cassandra etc. is still in its early stages and Kubernetes ecosystem is evolving very fast to support it. This project should serve as a good starting point for anyone who wants to get some basic understanding on how things work within Kubernetes and the interaction between different components deployed.

# Pipeline is K8S
![alt text](img/pipeline_in_k8s.png)
Following is the configuration for the Kubernetes cluster provisioned on AWS

* EKS Control Plane 
* Worker nodes with three m4.xlarge EC2 instances

The following are deployed as objects in K8S.

* Confluent Kafka
* Cassandra
* Spark Streaming
* Spark Batch + Airflow
* Python application as Kafka producer

Using Kafka to ingest messages, Spark Streaming to calculate user counts in 5 second intervals along with Spark batch jobs to calculate average page counts every minute as a baseline, and Cassandra to store the processed data to be queried.

# Getting Started
See the [wiki](https://github.com/sontivr/k8s-on-the-same-page/wiki/Getting-Started) page for instructions on getting started.

