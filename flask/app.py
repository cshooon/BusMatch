from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = ('')
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)


class BusStop(db.Model):
    __tablename__ = 'BusStops'
    id = db.Column(db.Integer, primary_key=True)
    route_id = db.Column(db.String(50))
    route_name = db.Column(db.String(100))
    sequence = db.Column(db.Integer)
    node_id = db.Column(db.String(50))
    stop_name = db.Column(db.String(100))


@app.route('/getRouteId/<route_name>')
def get_route_id(route_name):
    bus_stop = BusStop.query.filter_by(route_name=route_name).first()
    if bus_stop:
        return jsonify({'routeId': bus_stop.route_id})
    else:
        return jsonify({'error': 'Route not found'}), 404


@app.route('/getBusStopInfo/<stop_name>')
def get_bus_stop_info(stop_name):
    bus_stops = BusStop.query.filter_by(stop_name=stop_name).all()
    result = [{'sequence': bs.sequence, 'route_id': bs.route_id, 'node_id': bs.node_id} for bs in bus_stops]
    return jsonify({'busStops': result})


@app.route('/getRouteName/<route_id>')
def get_route_name(route_id):
    bus_stop = BusStop.query.filter_by(route_id=route_id).first()
    if bus_stop:
        return jsonify({'routeName': bus_stop.route_name})
    else:
        return jsonify({'error': 'Route ID not found'}), 404


@app.route('/')
def hello_world():  # put application's code here
    return 'Hello World!'


if __name__ == '__main__':
    app.run()
