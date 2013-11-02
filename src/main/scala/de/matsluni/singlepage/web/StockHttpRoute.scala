package de.matsluni.singlepage.web

import spray.routing.HttpService
import akka.actor.{ActorRef, ActorLogging, Props, Actor}
import spray.can.Http
import akka.io.IO
import de.matsluni.singlepage.web.StockHttpRouteActor._
import akka.util.Timeout
import akka.pattern.ask
import de.matsluni.singlepage.integration.{StockDataProducer}
import scala.reflect.ClassTag
import akka.camel.CamelMessage
import StockHttpRouteActor.Stocks
import StockHttpRouteActor.StockName
import scala.util.{Failure, Success}
import org.joda.time.DateTime
import com.github.nscala_time.time.StaticDateTimeFormat
import spray.httpx.encoding.Gzip
import akka.routing.RoundRobinRouter
import scala.concurrent.Future
import de.matsluni.singlepage.backend.{GetStockData, StoreStock, ReadStockNames}

object StockHttpRouteActor {

  /**
   * Factory for `akka.actor.Props` for [[de.matsluni.singlepage.web.StockHttpRouteActor]].
   */
  def props(interface: String, port: Int, _repoActor: ActorRef): Props =
    Props(new StockHttpRouteActor(interface, port, _repoActor))

  case class StockName(name: String)
  case class Price(value: Double, date: String)
  case class Stock(name: String, prices: List[Price])
  case class StockData(name: String, prices: List[(Long,Double)])
  case class Stocks(names: List[String])

  object StockNameProtocol {
    import spray.json.DefaultJsonProtocol._
    implicit val stockNameFormat = jsonFormat1(StockName)
  }

  object StocksProtocol {
    import spray.json.DefaultJsonProtocol._
    implicit val stocksFormat = jsonFormat1(Stocks)
  }

  object PriceProtocol {
    import spray.json.DefaultJsonProtocol._
    implicit val priceFormat = jsonFormat2(Price)
  }

  object StockDataProtocol {
    import spray.json.DefaultJsonProtocol._
    implicit val stockDataFormat = jsonFormat2(StockData)
  }

}

/**
 * This RouteActor will receive all requests from Clients and handles them accordingly.
 * @param interface The interface to bound to
 * @param port The port to listen on
 * @param _repoActor The actor for the repository
 */
class StockHttpRouteActor(interface: String, port: Int, _repoActor: ActorRef) extends Actor with StockHttpRoute with ActorLogging {

  IO(Http)(context.system) ! Http.Bind(self, interface, port)

  val repoActor = _repoActor

  val dataRetrievalActor = context.actorOf(StockDataProducer.props().withRouter(RoundRobinRouter(nrOfInstances = 5)),"dataProvider")

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(staticRoute ~ dynamicRoute)
}

/**
 * This is the trait where all http routes are defined.
 */
trait StockHttpRoute extends HttpService {

  // Self typing to make logging possible (see class QuoteSimpleServiceActor -> implementing ActorLogging)
  this: ActorLogging =>

  def repoActor: ActorRef

  def dataRetrievalActor: ActorRef

  implicit val timeout = Timeout(10000)

  implicit def executionContext = actorRefFactory.dispatcher

  val parser = StaticDateTimeFormat.forPattern("yyyy-MM-dd")

  // This is needed to encode Quote as JSON
  // import for using as un-marshaller for json post request
  import spray.httpx.SprayJsonSupport._
  import StockNameProtocol._
  import StocksProtocol._
  import StockDataProtocol._

  val staticRoute =
    path("") {
      getFromResource("index.html")
    } ~
    pathPrefix("public") {
      getFromResourceDirectory("public")
    }

  val dynamicRoute =
    pathPrefix("api") {
      // delivers a list of quotes from the repository
      path("stockNames") {
        get {
          complete {
            (repoActor ? ReadStockNames).mapTo[List[String]].map(Stocks)
          }
        }
      } ~
      // delivers the full history belonging to a stock
      path("fullStocks" / Segment ) { stockName =>
        (encodeResponse(Gzip) ) {
          get {
            dynamic {
              onComplete((repoActor ? GetStockData(stockName.name)).mapTo[Option[Stock]]) {
                _ match {
                  case Success(quote) => {
                    quote match {
                      case Some(q) => complete { StockData(q.name,q.prices.map(x => (parser.parseDateTime(x.date).getMillis(),x.value) )) }
                      case None => failWith(new IllegalArgumentException("Value not found"))
                    }
                  }
                  case Failure(e) => failWith(e)
                }
              }
            }
          }
        }
      } ~
      // receives a stock for which the history has to be looked up and stored in the repository
      path("stockName") {
        post {
          entity(as[StockName]) { stockName =>
            onComplete((dataRetrievalActor ? CamelMessage(stockName.name.toUpperCase(),Map.empty)).mapTo[Stock]) {
              _ match {
                case Success(s) => complete { repoActor ! StoreStock(s); "ok" }
                case Failure(e) => failWith(e)
              }
            }
          }
        }
      }
    }
}

