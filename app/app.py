import dash
from dash.dependencies import Output, Event
import dash_core_components as dcc
import dash_html_components as html
import plotly
import random
import plotly.graph_objs as go
from collections import deque
import cassandra_data
import datetime
import pandas as pd

host = '52.73.46.190'
keyspace = 'site_log'

session = cassandra_data.start_connection(host, keyspace)
prep_query = cassandra_data.prepare_page_visits_query(session)
page = 'http://myfancysite.com/page760'

app = dash.Dash(__name__)
app.layout = html.Div(
    [
        dcc.Graph(id='live-graph', animate=True),
        dcc.Interval(
            id='graph-update',
            interval=1*1000
        ),
    ]
)

@app.callback(Output('live-graph', 'figure'),
              events=[Event('graph-update', 'interval')])
def update_graph_scatter():
    df = cassandra_data.get_page_visit_count(page, prep_query, session)
    X = df.interval_time.values[-20:]
    Y = df.visits.values[-20:]

    data = plotly.graph_objs.Scatter(
            x=list(X),
            y=list(Y),
            name='Scatter',
            mode= 'lines+markers'
            )

    return {'data': [data],'layout' : go.Layout(xaxis=dict(range=[min(X),max(X)]),
                                                yaxis=dict(range=[min(Y),max(Y)]),)}



if __name__ == '__main__':
    app.run_server(debug=True)