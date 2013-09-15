package de.matsluni.singlepage.web

import spray.routing.HttpService
import akka.actor.{ActorRef, ActorLogging, Props, Actor}
import spray.can.Http
import akka.io.IO
import de.matsluni.singlepage.web.QuoteHttpRouteActor._
import akka.util.Timeout
import akka.pattern.ask
import de.matsluni.singlepage.integration.QuoteDataProducer
import scala.reflect.ClassTag
import akka.camel.CamelMessage
import QuoteHttpRouteActor.Quotes
import QuoteHttpRouteActor.QuoteName
import scala.util.{Failure, Success}
import org.joda.time.DateTime
import com.github.nscala_time.time.StaticDateTimeFormat
import spray.httpx.encoding.Gzip
import akka.routing.RoundRobinRouter
import scala.concurrent.Future

object QuoteHttpRouteActor {

  /**
   * Factory for `akka.actor.Props` for [[de.matsluni.singlepage.web.QuoteHttpRouteActor]].
   */
  def props(interface: String, port: Int, _repoActor: ActorRef): Props =
    Props(new QuoteHttpRouteActor(interface, port, _repoActor))

  case class QuoteName(name: String)
  case class Price(value: Double, date: String)
  case class Quote(name: String, prices: List[Price])
  case class QuoteData(name: String, prices: List[(Long,Double)])
  case class Quotes(names: List[String])

  object QuoteNameProtocol {
    import spray.json.DefaultJsonProtocol._
    implicit val quoteNameFormat = jsonFormat1(QuoteName)
  }

  object QuotesProtocol {
    import spray.json.DefaultJsonProtocol._
    implicit val quotesFormat = jsonFormat1(Quotes)
  }

  object PriceProtocol {
    import spray.json.DefaultJsonProtocol._
    implicit val priceFormat = jsonFormat2(Price)
  }

  object QuoteDataProtocol {
    import spray.json.DefaultJsonProtocol._
    implicit val quoteDataFormat = jsonFormat2(QuoteData)
  }

}

/**
 * This RouteActor will receive all requests from Clients and handles them accordingly.
 * @param interface The interface to bound to
 * @param port The port to listen on
 * @param _repoActor The actor for the repository
 */
class QuoteHttpRouteActor(interface: String, port: Int, _repoActor: ActorRef) extends Actor with QuoteHttpRoute with ActorLogging {

  IO(Http)(context.system) ! Http.Bind(self, interface, port)

  val repoActor = _repoActor

  val dataRetrievalActor = context.actorOf(QuoteDataProducer.props().withRouter(RoundRobinRouter(nrOfInstances = 5)),"dataProvider")

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
trait QuoteHttpRoute extends HttpService {

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
  import QuoteNameProtocol._
  import QuotesProtocol._
  import QuoteDataProtocol._

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
      path("quoteNames") {
        get {
          complete {
            (repoActor ? ReadQuoteNames).mapTo[List[String]].map(Quotes)
          }
        }
      } ~
      // delivers the full history belonging to a quote
      path("fullQuotes" / Segment ) { quoteName =>
        (encodeResponse(Gzip) ) {
          get {
            dynamic {
              onComplete((repoActor ? GetQuoteData(quoteName.name)).mapTo[Option[Quote]]) {
                _ match {
                  case Success(quote) => {
                    quote match {
                      case Some(q) => complete { QuoteData(q.name,q.prices.map(x => (parser.parseDateTime(x.date).getMillis(),x.value) )) }
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
      // receives a quote for which the history has to be looked up and stored in the repository
      path("quoteName") {
        post {
          entity(as[QuoteName]) { quoteName =>
            onComplete((dataRetrievalActor ? CamelMessage(quoteName.name.toUpperCase(),Map.empty)).mapTo[Quote]) {
              _ match {
                case Success(s) => complete { repoActor ! StoreQuote(s); "ok" }
                case Failure(e) => failWith(e)
              }
            }
          }
        }
      }
    }
}

