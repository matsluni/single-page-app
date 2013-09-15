package de.matsluni.singlepage.web

import scala.collection.mutable
import java.util.Date
import akka.actor.{ActorLogging, Actor}
import de.matsluni.singlepage.web.QuoteSimpleServiceActor.Quote

sealed trait Command
case object ReadQuoteNames extends Command
case class StoreQuote(quote: Quote) extends Command
case class GetQuoteData(quote: String) extends Command

class RepositoryActor extends Actor with ActorLogging { this: Repository =>

  def receive: PartialFunction[Any, Unit] = {
    case ReadQuoteNames => {
      sender ! listQuotes()
    }
    case StoreQuote(q) => {
      log.debug(s"Store quote: ${q.name}")
      storeQuote(q)
    }
    case GetQuoteData(q) => {
      sender ! getQuoteByName(q)
    }
  }
}

trait Repository {
  def getQuoteByName(name: String): Option[Quote]
  def storeQuote(quote : Quote) : Unit
  def listQuotes(): List[String]
}

trait InMemoryRepository extends Repository {

  val stocks = new mutable.HashMap[String,Quote]()

  override def getQuoteByName(name: String): Option[Quote] = {
    return stocks.get(name)
  }

  override def storeQuote(stock: Quote) {
    stocks.put(stock.name,stock)
  }

  override def listQuotes() = stocks.keySet.toList.sorted
}


