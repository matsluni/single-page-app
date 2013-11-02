## Single page app with spray, Akka, Camel, Backbone, Bootstrap

This is a little demo app which I made to make myself more familiar with:
* spray (spray.io)
* Akka (akka.io)
* Apache Camel (camel.apache.org)
* Backbone.js (backbonejs.org)
* Bootstrap (getbootstrap.com)
* RequireJS (requirejs.org)

What does this application? It just shows some historical stockdata from stock-symbols you can enter. Right now the
stockdata is just stored in a simple HashMap. The saved quotedata is served as a json-document by spray-can.

To start the app just start SBT (scala-sbt.org) in root folder of application and then start the application with **re-start** command.
This starts spray-can on the given **port** in _src/main/resources/application.conf_. Here you also can specify the
**startdate** and the **enddate** for the historical data to be retrieved from the internet.

Further improvements:
* add a durable storage engine (e.g. RDBMS, NOSQL, Cassandra)
* improve/clean REST-API on backend and frontend
* cleanup some stuff in Backbone.js frontend
* add more functionality