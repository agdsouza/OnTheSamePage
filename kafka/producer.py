import sys
import numpy
from faker import Faker
from datetime import datetime
from kafka.client import KafkaClient
from kafka.producer import KafkaProducer

TOPIC_NAME = 'site_log'
WEBSITE_NAME = "http://myfancysite.com/"

class Producer(object):

    def __init__(self, addr):
        self.producer = KafkaProducer(bootstrap_servers=addr)

    def produce_msgs(self, source_symbol):
        while True:
            # generate random values for the IP and URL, and get the current time for the timestamp
        	ip_field = numpy.random.choice(ips)
        	url_field = WEBSITE_NAME + "page" + str(numpy.random.randint(1,1001))
        	time_field = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

            # produce to the topic indicated in TOPIC_NAME
        	str_fmt = "{};{};{};{}"
        	message_info = str_fmt.format(source_symbol, time_field, ip_field, url_field)
        	self.producer.send(TOPIC_NAME, message_info)

if __name__ == "__main__":
	faker = Faker()
	ips = [faker.ipv4() for _ in range(5000)]
	args = sys.argv
	ip_addr = str(args[1])
	partition_key = str(args[2])
	prod = Producer(ip_addr)
	prod.produce_msgs(partition_key) 
