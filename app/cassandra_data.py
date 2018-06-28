from cassandra.cluster import Cluster
import pandas as pd
import time
import datetime

host = '52.73.46.190'
keyspace = 'site_log'
page = 'http://myfancysite.com/page760'

def start_connection(host, keyspace):
	cluster = Cluster([host])
	session = cluster.connect(keyspace)
	return session

def prepare_page_visits_query(session):
	query = "SELECT * FROM page_visit_table WHERE url=? LIMIT 20"
	return session.prepare(query)

def get_page_visit_count(page, prepared_query, session):
	count = session.execute(prepared_query, [page])
	df = pd.DataFrame(list(count))
	return df
