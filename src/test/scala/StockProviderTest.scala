import akka.actor.ActorSystem
import akka.camel.CamelMessage
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import akka.util.Timeout
import de.matsluni.singlepage.integration.{StockDataProducer, FileSymbolConsumer}
import de.matsluni.singlepage.web.StockHttpRouteActor.Stock
import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfterAll, WordSpec,BeforeAndAfter}
import scala.concurrent.duration.FiniteDuration
import scala.reflect.io.Path

/**
 * Class to test the QuoteProviderActors
 */
class StockProviderTest extends TestKit(ActorSystem()) with WordSpec with MustMatchers with BeforeAndAfterAll with ImplicitSender {

  val basePath = "src/test/resources/in"

  override def beforeAll() {
    Path(basePath+"/quotes.txt").toFile.writeAll("AAPL\nMSFT")
  }

  "A FileConsumer" must {
    TestActorRef(new FileSymbolConsumer("file:"+basePath+"/?noop=true",testActor))
    "read the ALL the Quotes from given location and send them as separate messages to the target actor" in  {
      expectMsgAllOf("AAPL","MSFT")
    }
  }


  "A Producer" must {
    val dataActor = TestActorRef(new StockDataProducer())
    "deliver stock data for the given stock name from the producer uri" in {
      dataActor ! CamelMessage("AAPL",Map.empty)
      expectMsgType[Stock]
    }
  }
}

