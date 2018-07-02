from cassandra.cluster import Cluster
from cassandra.query import dict_factory
from cassandra import ReadTimeout
import pandas as pd
import time
import datetime

def start_connection(host, keyspace):
	cluster = Cluster([host])
	session = cluster.connect(keyspace)
	session.row_factory = dict_factory
	return session

def prepare_page_visits_query(session):
	query = "SELECT * FROM page_visit_table WHERE url=? LIMIT 100"
	return session.prepare(query)

def get_page_visit_count(page, prepared_query, session):
	count = session.execute_async(prepared_query, [page])
	try:
		rows = count.result()
		df = pd.DataFrame(list(rows))
	except ReadTimeout:
	    log.exception("Query timed out:")
	return df

def prepare_avg_visits_query(session):
	query = "SELECT * FROM page_averages WHERE url=? AND average>4 LIMIT 1 ALLOW FILTERING"
	return session.prepare(query)

def get_page_averages(page, prepared_query, session):
	avg = session.execute_async(prepared_query, [page])
	try:
		rows = avg.result()
		df = pd.DataFrame(list(rows))
	except ReadTimeout:
	    log.exception("Query timed out:")
	return df