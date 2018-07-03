# airflow_spark_dag.py
from airflow import DAG
from airflow.operators.bash_operator import BashOperator
from datetime import datetime, timedelta
import os

## Define the DAG object
default_args = {
        'owner': 'insight-adriana',
        'depends_on_past': False,
        'start_date': datetime(2018, 6, 27),
        'retries': 5,
        'retry_delay': timedelta(seconds=5),
    }

dag = DAG('sparkBatchMinute', default_args=default_args, schedule_interval='*/1 * * * *')

# task to run a Spark job every minute
averagePageCounts = BashOperator(
    task_id='average-page',
    bash_command='~/scripts/average_page.sh',
    dag=dag)