## Single page app with spray, Akka, Camel, Backbone, Bootstrap

This is a little demo app which I made to make myself more familiar with:
* spray (spray.io)
* Akka (akka.io)
* Apache Camel (camel.apache.org)
* Backbone.js ()
* Bootstrap (getbootstrap.com)

What does this application? It just shows some historical stockdata from stockquotes you can enter. Right now the
stockdata is just stored in a simple HashMap. The saved quotedata is served as a json-document by spray-can.

To start the app just start SBT in root folder of application and then start the application with _re-start_ command.
This starts spray-can on the given _port_ in src/main/resources/application.conf. Here you also can specify the
_startdate_ and the _enddate_ for the historical data to be retrieved from the internet.

Further improvements:
* add a durable storage engine (e.g. RDBMS, NOSQL, Cassandra)
* improve/clean REST-API on backend and frontend
* cleanup some stuff in Backbone.js frontend
* add more functionality