#!/bin/bash


echo "Starting Spark batch job"

export DRIVER_NAME=spark-batch
if [ ! -z "$SPARK_DRIVER_NAME" ]
then
    export DRIVER_NAME=$SPARK_DRIVER_NAME
fi

export DRIVER_PORT=29513
if [ ! -z "$SPARK_DRIVER_PORT" ]
then
    export DRIVER_PORT=$SPARK_DRIVER_PORT
fi

export SA=spark-sa-dr
if [ ! -z "$SPARK_DRIVER_SA" ]
then
    export SA=$SPARK_DRIVER_SA
fi

export NAMESPACE=default
if [ ! -z "$NAMESPACE" ]
then
    export NAMESPACE=$NAMESPACE
fi

CASSANDRA_CONNECT_CONF=
if [ ! -z $CASSANDRA_HOST ]
then
    CASSANDRA_CONNECT_CONF="--conf spark.cassandra.connection.host=$CASSANDRA_HOST" 
fi

SPARK_EXECUTOR_COUNT=3
if [ ! -z "$EXECUTOR_COUNT" ]
then
    SPARK_EXECUTOR_COUNT=$EXECUTOR_COUNT
fi


export K8S_CACERT=/var/run/secrets/kubernetes.io/serviceaccount/ca.crt
export K8S_TOKEN=/var/run/secrets/kubernetes.io/serviceaccount/token
export DOCKER_IMAGE=sontivr/spark-2.4.0-bin-hadoop2.7:latest

kubectl expose deployment $DRIVER_NAME --port=$DRIVER_PORT --type=ClusterIP --cluster-ip=None

/opt/spark/bin/spark-submit --name spark-batch \
                --master k8s://https://kubernetes.default.svc.cluster.local:443  \
                --deploy-mode client  \
                --class site_batch \
                --conf spark.kubernetes.authenticate.caCertFile=/var/run/secrets/kubernetes.io/serviceaccount/ca.crt  \
                --conf spark.kubernetes.authenticate.oauthTokenFile=/var/run/secrets/kubernetes.io/serviceaccount/token  \
                --conf spark.kubernetes.authenticate.driver.serviceAccountName=$SA  \
                --conf spark.kubernetes.namespace=$NAMESPACE  \
                --conf spark.executor.instances=$SPARK_EXECUTOR_COUNT  \
                --conf spark.kubernetes.container.image=$DOCKER_IMAGE  \
                --conf spark.driver.host=$DRIVER_NAME.$NAMESPACE.svc.cluster.local  \
                --conf spark.driver.port=$DRIVER_PORT  \
		$CASSANDRA_CONNECT_CONF \
		--packages datastax:spark-cassandra-connector:2.4.0-s_2.11 \
		local:///opt/spark/apps/scripts/site_batch_2.11-1.0.jar 

echo "Spark batch job completed"

