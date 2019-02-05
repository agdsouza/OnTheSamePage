import dash
import dash.dependencies
import dash_core_components as dcc
import dash_html_components as html
import plotly
import random
import plotly.graph_objs as go
from collections import deque
import cassandra_data
from datetime import datetime
import pandas as pd
import flask
import numpy as np

# HOST = '52.73.46.190'
# no need to hardcode cassandra's host ip as it can be accessed localhost through port forwarding
HOST = 'localhost'
KEYSPACE = 'site_log'
WEBSITE = 'http://myfancysite.com/'

session = cassandra_data.start_connection(HOST, KEYSPACE)
prep_page_query = cassandra_data.prepare_page_visits_query(session)
prep_avg_query = cassandra_data.prepare_avg_visits_query(session)

PAGES = ['page' + str(n) for n in range(1, 1001)]

POINTS_MIN = 10
POINTS_MAX = 100

server = flask.Flask(__name__)
app = dash.Dash(__name__, server=server)

app.layout = html.Div(
    [
        html.Div([
        html.H2('On the Same Page',
                style={'float': 'left',
                       }),
        ]),
    dcc.Input(id='num_points', value='', type='text'),
        dcc.Dropdown(id='page_name',
             options=[{'label': p, 'value': p} for p in PAGES],
             multi=True
         ),
    html.Div(children=html.Div(id='graphs'), className='row'),
        dcc.Interval(
            id='graph-update',
            interval=1*1000
        ),
    ], className="container",style={'width':'98%','margin-left':10,'margin-right':10,'max-width':50000}
)

@app.callback(
    dash.dependencies.Output('graphs', 'children'),
    [dash.dependencies.Input('num_points', 'value'),
    dash.dependencies.Input('page_name', 'value')],
    events=[dash.dependencies.Event('graph-update', 'interval')])
def update_graph(num_points, page_name):
    graphs = []

    # configure input for number of points to be displayed on the UI
    try: 
        points = int(num_points)
        if points < POINTS_MIN:
            points = POINTS_MIN
        elif points > POINTS_MAX:
            points = POINTS_MAX
    except:
        points = POINTS_MIN

    # alter display of graphs depending on how many graphs are chosen to be shown
    if len(page_name)>2:
        class_choice = 'col s12 m6 l4'
    elif len(page_name) == 2:
        class_choice = 'col s12 m6 l6'
    else:
        class_choice = 'col s12'

    for p in page_name:
        # dataframe for the page views every 5 seconds
        df1 = cassandra_data.get_page_visit_count(WEBSITE + p, prep_page_query, session)
        df1['interval_time'] = df1['interval_time'].map(lambda x: \
            str(x - np.timedelta64(4,'h'))[:10] + " " + str(x - np.timedelta64(4,'h'))[11:19])

        X = df1.interval_time.values[:points]
        Y = df1.visits.values[:points]

        # dataframe for the average page views every 5 seconds
        df2 = cassandra_data.get_page_averages(WEBSITE + p, prep_avg_query, session)
        avg_line = df2.average.values[0]

        data = plotly.graph_objs.Scatter(
                x=list(X),
                y=list(Y),
                name='Scatter',
                mode= 'lines+markers'
                )

        graphs.append(html.Div(dcc.Graph(
            id=p,
            animate=True,
            figure={'data': [data],'layout' : go.Layout(xaxis=dict(range=[min(X),max(X)], tickformat='%H:%M:%S'),
                                                yaxis=dict(range=[min(Y),max(Y)]),
                                                title=p,
                                                shapes=[
                                                        {
                                                            'type': 'line',
                                                            'xref': 'paper',
                                                            'x0': 0,
                                                            'y0': avg_line, # use absolute value or variable here
                                                            'x1': 1,
                                                            'y1': avg_line, # ditto
                                                            'line': {
                                                                'color': 'rgb(50, 171, 96)',
                                                                'width': 1,
                                                                'dash': 'dash',
                                                            },
                                                        },
                                                    ])}), className=class_choice))
    return graphs

external_css = ["https://cdnjs.cloudflare.com/ajax/libs/materialize/0.100.2/css/materialize.min.css"]
for css in external_css:
    app.css.append_css({"external_url": css})

external_js = ['https://cdnjs.cloudflare.com/ajax/libs/materialize/0.100.2/js/materialize.min.js']
for js in external_css:
    app.scripts.append_script({'external_url': js})

if __name__ == '__main__':
    app.run_server(host="0.0.0.0", port=80)


    
