package de.matsluni.singlepage.integration

import akka.camel.{CamelMessage, Consumer, Producer}
import akka.actor._
import org.apache.camel.Exchange
import scala.collection.immutable.HashMap
import java.text.SimpleDateFormat
import akka.pattern.ask
import akka.util.Timeout
import akka.event.Logging
import java.util.Date
import de.matsluni.singlepage.SinglePageSettings
import org.joda.time.DateTime
import com.github.nscala_time.time.StaticDateTimeFormat
import de.matsluni.singlepage.web.StockHttpRouteActor.{Price, Stock}

/**
 * Consumes data from 'from' and then sends camel messages to target with help of the 'receive'-method,
 * which is implicitly called with consumed data. In this case the whole file will be consumed and the
 * content will be put in the body of the CamelMessage.
 * @param from Endpoint consume data from
 * @param target The actorRef to send the produced data to
 */
class FileSymbolConsumer(from: String, target: ActorRef) extends Consumer {

  def endpointUri = from  // can be "file://in/"

  def receive = {
    case msg: CamelMessage => msg.bodyAs[String].split("\n").foreach(target ! _)
  }
}

/**
 * Produces data from the yahoo-finance endpoint. It receives a stock symbol in the body of the camel message.
 * The received data is splitted by newline and converted to a list before forwarded to a parser actor.
 */
class StockDataProducer() extends Actor with Producer with ActorLogging {

  val dataParserActor = context.actorOf(StockDataParser.props(),"dataParser")

  val startDate = SinglePageSettings(context.system).startdate //"&a=00&b=1&c=2012" == 01.01.2012
  val endDate = SinglePageSettings(context.system).enddate //"&d=06&e=31&f=2013" == 31.07.2013

  def endpointUri = "http4://ichart.yahoo.com/table.csv"

  // this is called before the message is send to the endpoint to produce the data
  override def transformOutgoingMessage(msg: Any) = msg match {
    case msg: CamelMessage =>
      val symbol = msg.bodyAs[String]
      val queryString = s"s=$symbol${getStartDateFormatted(startDate)}${getEndDateFormatted(endDate)}&g=d&ignore=.csv"

      msg.copy(headers = HashMap(Exchange.HTTP_QUERY -> queryString, "SYMBOL" -> symbol))
  }

  // routeResponse is getting called after data was produced by calling the endpoint.
  // sending the result from the request against uri to target actor (with body content as String)
  override def routeResponse(msg: Any) = msg match {
    case msg: CamelMessage =>
      val symbol = msg.headers(Set("SYMBOL")).getOrElse("SYMBOL", "")
      dataParserActor.forward((symbol, msg.bodyAs[String].split("\n").tail.toList)) // skipping first (.tail...) because : is a csv header
  }

  private def getStartDateFormatted(startDate: DateTime) =
    s"&a=${startDate.monthOfYear().get()-1}&b=${startDate.dayOfMonth().get()}&c=${startDate.year().get()}"

  private def getEndDateFormatted(endDate: DateTime) =
    s"&d=${endDate.monthOfYear().get()-1}&e=${endDate.dayOfMonth().get()}&f=${endDate.year().get()}"
}

/**
 * This is a actor which solely purpose is to transform the csv-rows from QuoteDataProducer
 * into the case class Quote. It reads the date (first column) a the closing value (last column) from each cvs-row.
 */
class StockDataParser() extends Actor with ActorLogging {

  type Symbol = String

  implicit val timeout = Timeout(8000)
  val parser = StaticDateTimeFormat.forPattern("yyyy-MM-dd")

  override def receive = {
    case quoteDataList: (Symbol, List[String]) =>
//      log.debug(quoteDataList.toString())
      sender ? getQuote(quoteDataList._1, quoteDataList._2)
  }

  def getQuote(symbol: String, body: List[String]): Stock = {

    def getPrice(l: List[String], priceList: List[Price]): List[Price] = {
      if (l.isEmpty) priceList
      else {
        val row = l.head.split(",")
        // Date is first index (0), actual price is last index (length-1)
        getPrice(l.tail, new Price(row(row.length-1).toDouble, row(0)) :: priceList)
//        getPrice(l.tail, new Price(row(row.length-1).toDouble, parser.parseDateTime(row(0)).toString("dd.MM.yyyy") ) :: priceList)
      }
    }
    new Stock(symbol, getPrice(body, Nil))
  }
}

object StockDataProducer {

  /**
   * Factory for `akka.actor.Props` for [[de.matsluni.singlepage.integration.StockDataProducer]].
   */
  def props(): Props =
    Props(new StockDataProducer())
}

object StockDataParser {

  /**
   * Factory for `akka.actor.Props` for [[de.matsluni.singlepage.integration.StockDataParser]].
   */
  def props(): Props =
    Props(new StockDataParser())
}
