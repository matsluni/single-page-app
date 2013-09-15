package de.matsluni.singlepage.web

import spray.routing.HttpService
import akka.actor.{ActorRef, ActorLogging, Props, Actor}
import spray.can.Http
import akka.io.IO
import de.matsluni.singlepage.web.QuoteSimpleServiceActor._
import akka.util.Timeout
import akka.pattern.ask
import de.matsluni.singlepage.integration.QuoteDataProducer
import scala.reflect.ClassTag
import akka.camel.CamelMessage
import de.matsluni.singlepage.web.QuoteSimpleServiceActor.Quotes
import de.matsluni.singlepage.web.QuoteSimpleServiceActor.QuoteName
import scala.util.{Failure, Success}
import org.joda.time.DateTime
import com.github.nscala_time.time.StaticDateTimeFormat
import spray.httpx.encoding.Gzip
import akka.routing.RoundRobinRouter
import scala.concurrent.Future

object QuoteSimpleServiceActor {

  /**
   * Factory for `akka.actor.Props` for [[de.matsluni.singlepage.SinglePageSettings]].
   */
  def props(interface: String, port: Int, _repoActor: ActorRef): Props =
    Props(new QuoteSimpleServiceActor(interface, port, _repoActor))

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

  object QuoteProtocol {
    import spray.json.DefaultJsonProtocol._
    import PriceProtocol._
    implicit val quoteFormat = jsonFormat2(Quote)
  }

  object QuoteDataProtocol {
    import spray.json.DefaultJsonProtocol._
    implicit val quoteDataFormat = jsonFormat2(QuoteData)
  }

}

class QuoteSimpleServiceActor(interface: String, port: Int, _repoActor: ActorRef) extends Actor with QuoteSimpleService with ActorLogging {

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

trait QuoteSimpleService extends HttpService {

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
  import QuotesProtocol._
  import QuoteNameProtocol._
  import QuoteProtocol._
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
      path("quoteNames") {
        get {
          complete {
            (repoActor ? ReadQuoteNames).mapTo[List[String]].map(Quotes)
          }
        }
      } ~
      path("fullQuotes" / Segment ) { quoteName =>
        (encodeResponse(Gzip) ) {
          get {
            dynamic {
              onComplete((repoActor ? GetQuoteData(quoteName.name)).mapTo[Option[Quote]]) {
                _ match {
                  case Success(quote) => {
                    quote match {
                      case Some(q) => complete { QuoteData(q.name,q.prices.map(x => (parser.parseDateTime(x.date).getMillis(),x.value) )) }
                    }
                  }
                }
              }
            }
          }
        }
      } ~
      path("quoteName") {
        post {
          entity(as[QuoteName]) { quoteName =>
            onComplete((dataRetrievalActor ? CamelMessage(quoteName.name.toUpperCase(),Map.empty)).mapTo[Quote]) {
              _ match {
                case Success(s) => complete { log.debug(s"save quote data for $quoteName"); repoActor ! StoreQuote(s); "ok" }
                case Failure(e) => failWith(e)
              }
            }
          }
        }
      }
    }
}

