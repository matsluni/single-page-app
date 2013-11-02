package de.matsluni.singlepage.backend

import scala.collection.mutable
import java.util.Date
import akka.actor.{ActorLogging, Actor}
import de.matsluni.singlepage.web.StockHttpRouteActor.{Price, Stock}

sealed trait Command
case object ReadStockNames extends Command
case class StoreStock(stock: Stock) extends Command
case class GetStockData(stock: String) extends Command

class RepositoryActor extends Actor with ActorLogging { this: Repository =>

  def receive: PartialFunction[Any, Unit] = {
    case ReadStockNames => {
      sender ! listStocks()
    }
    case StoreStock(q) => {
      log.debug(s"Store stock: ${q.name}")
      storeStock(q)
    }
    case GetStockData(q) => {
      sender ! getStockByName(q)
    }
  }
}

trait Repository {
  def getStockByName(name: String): Option[Stock]
  def storeStock(stock : Stock) : Unit
  def listStocks(): List[String]
}

trait InMemoryRepository extends Repository {

  val stocks = new mutable.HashMap[String,Stock]()

  override def getStockByName(name: String): Option[Stock] = {
    return stocks.get(name)
  }

  override def storeStock(stock: Stock) {
    stocks.put(stock.name,stock)
  }

  override def listStocks() = stocks.keySet.toList.sorted
}

trait TestRepository extends Repository {

  override def getStockByName(name: String): Option[Stock] = {
    return Some(new Stock("AAPL",List(new Price(0.5,"2001-01-01"),new Price(0.6,"2001-01-02"),new Price(0.4,"2001-01-03"))))
  }

  override def storeStock(stock: Stock) {
    Some(stock)
  }

  override def listStocks() = List("AAPL","ORCL","MSFT","FB")
}
