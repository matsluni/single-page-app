import akka.actor.ActorSystem
import akka.camel.CamelMessage
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import akka.util.Timeout
import de.matsluni.singlepage.integration.{QuoteDataProducer, FileSymbolConsumer}
import de.matsluni.singlepage.web.Models.Quote
import de.matsluni.singlepage.web.{StoreQuote, InMemoryRepository, RepositoryActor}
import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfterAll, WordSpec,BeforeAndAfter}
import scala.concurrent.duration.FiniteDuration
import scala.reflect.io.Path

/**
 * Class to test the QuoteProviderActors
 */
class QuoteProviderTest extends TestKit(ActorSystem()) with WordSpec with MustMatchers with BeforeAndAfterAll with ImplicitSender {

  val basePath = "src/test/resources/in"

  override def beforeAll() {
    Path(basePath+"/quotes.txt").toFile.writeAll("AAPL\nMSFT")
  }

//  "A FileConsumer" must {
//    TestActorRef(new FileSymbolConsumer("file:"+basePath+"/?noop=true",testActor))
//    "read the Quotes from given location and send them to the target actor" in  {
//      expectMsg("AAPL")
//    }
//  }



  "A FileConsumer" must {
    TestActorRef(new FileSymbolConsumer("file:"+basePath+"/?noop=true",testActor))
    "read the ALL the Quotes from given location and send them as separate messages to the target actor" in  {
      expectMsgAllOf("AAPL","MSFT")
    }
  }


  "A Producer" must {
    val dataActor = TestActorRef(new QuoteDataProducer())
    "deliver quote data for the given quote name from the producer uri" in {
      dataActor ! CamelMessage("AAPL",Map.empty)
      expectMsgType[Quote]
//      expectMsg(FiniteDuration.apply(5000,"ms"),List())
    }
  }
}


//class SimpleActor extends Actor {
//  override def receive = {
//    case x => println(x)
//  }
//}
//
//
//// we need an ActorSystem to host our application in
//val system = ActorSystem("test-system")
//val simpleActor = system.actorOf(Props[SimpleActor], "targetActor")
//val stockDataParser = system.actorOf(Props(new QuoteDataParser(simpleActor)), "stockDataParser")
//val dataActor: ActorRef = system.actorOf(QuoteDataProducer.props(stockDataParser), "dataprovider")
//
//dataActor ! CamelMessage("AAPL",Map.empty)