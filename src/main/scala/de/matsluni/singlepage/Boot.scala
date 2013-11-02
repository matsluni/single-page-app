package de.matsluni.singlepage

import akka.actor.{ActorSystem, Props}
import de.matsluni.singlepage.web.StockHttpRouteActor
import de.matsluni.singlepage.backend.{TestRepository, InMemoryRepository, RepositoryActor}

object Boot extends App {

  // we need an ActorSystem to host our application in
  val system = ActorSystem("singlepage-system")

  // get config from settings
  val interface = SinglePageSettings(system).interface
  val port = SinglePageSettings(system).port

//  val repoActor = system.actorOf(Props(new RepositoryActor() with InMemoryRepository),"repo-actor")
  val repoActor = system.actorOf(Props(new RepositoryActor() with TestRepository),"repo-actor")

//  val stockDataParser = system.actorOf(Props(new QuoteDataParser()), "stockDataParser")
//  val dataActor = system.actorOf(QuoteDataProducer.props(), "dataprovider")

  // create and start our service actor
  system.actorOf(StockHttpRouteActor.props(interface,port,repoActor), "demo-service")
}