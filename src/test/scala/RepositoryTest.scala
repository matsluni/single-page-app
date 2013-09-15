import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestActorRef}
import de.matsluni.singlepage.web.Models.{Price, Quote}
import de.matsluni.singlepage.web._
import de.matsluni.singlepage.web.StoreQuote
import java.util.Date
import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfterAll, WordSpec}
import scala.Some

/**
 * Class to test the RepositoryActor
 */
class RepositoryTest extends TestKit(ActorSystem()) with WordSpec with MustMatchers with BeforeAndAfterAll  {

  "A RepositoryActor with InMemoryRepository" must {
    val actorRef = TestActorRef(new RepositoryActor with InMemoryRepository)
    "store the message in the repository" in {
      // This call is synchronous. The actor receive() method will be called in the current thread
      actorRef ! StoreQuote(Quote("AAPL",Nil))
      // With actorRef.underlyingActor, we can access the SimpleActor instance created by Akka
      actorRef.underlyingActor.listQuotes() must equal(List("AAPL"))

    }
  }

  "A RepositoryActor with TestRepository" must {
    val actorRef = TestActorRef(new RepositoryActor with TestRepository)
    "return the correct result of the TestRepository" in {
      // This call is synchronous. The actor receive() method will be called in the current thread
      actorRef ! ReadQuoteNames
      // With actorRef.underlyingActor, we can access the SimpleActor instance created by Akka
      actorRef.underlyingActor.listQuotes() must equal(List("AAPL","ORCL","MSFT","FB"))

    }
  }

}

trait TestRepository extends Repository {

  override def getQuoteByName(name: String): Option[Quote] = {
    return Some(new Quote("AAPL",List(new Price(0.5,new Date(0,1,2001)),new Price(0.6,new Date(0,2,2001)),new Price(0.4,new Date(0,2,2001)))))
  }

  override def storeQuote(stock: Quote) {

  }

  override def listQuotes() = List("AAPL","ORCL","MSFT","FB")
}
