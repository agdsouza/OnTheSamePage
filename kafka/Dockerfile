FROM python:3.7

ENV PYTHONUNBUFFERED 1
COPY requirements.txt .
COPY producer.py .
RUN pip install -r requirements.txt
CMD ["bash"]
