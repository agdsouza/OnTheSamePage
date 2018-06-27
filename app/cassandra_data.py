from cassandra.cluster import Cluster
import pandas as pd
import time
import datetime

host = '52.73.46.190'
keyspace = 'site_log'
page = 'http://myfancysite.com/page760'
# cluster = Cluster(['52.73.46.190'])
# session = cluster.connect('site_log')

# #t = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

# c = 0
# query = "SELECT interval_time, visits FROM page_visit_table WHERE url='http://myfancysite.com/page760' AND interval_time=?"
# interval_statement = session.prepare(query)

# user_counts = []
# while True:
# 	t = (datetime.datetime.now() + datetime.timedelta(hours=4)).replace(microsecond=0)
# 	print(t)
# 	count = session.execute(interval_statement, [t])
# 	print(count == None)
# 	print(count.current_rows)
# 	# for i in count:
# 		# print i.interval_time, i.visits
# 	user_counts.append(count)
# 	print(len(user_counts))
# 	c += 1
# 	if c == 100000:
# 		break

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

# session = start_connection(host, keyspace)
# prep_query = prepare_page_visits_query(session)
# t = (datetime.datetime.now() + datetime.timedelta(hours=4)).replace(microsecond=0)
# print(get_page_visit_count(t, 'http://myfancysite.com/page760', prep_query, session))


# start = time.time()
# rows = session.execute("SELECT url, starttime FROM raw_data WHERE url='http://myfancysite.com/page760' AND starttime = '2018-06-26 06:32:54'")
# end = time.time()

# print("Query executed in time: {}").format(end - start)

# if rows == None:
# 	for u in rows:
# 		print u.timestamp
# else:
# 	print "hey there"

# from cassandra import ReadTimeout

# query = "SELECT * FROM users WHERE user_id=%s"
# future = session.execute_async(query, [user_id])

# # ... do some other work

# try:
#     rows = future.result()
#     user = rows[0]
#     print user.name, user.age
# except ReadTimeout:
#     log.exception("Query timed out:")



#def get_page_count(timestamp, page):
