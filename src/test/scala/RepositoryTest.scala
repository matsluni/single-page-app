import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestActorRef}
import de.matsluni.singlepage.backend._
import de.matsluni.singlepage.backend.StoreStock
import de.matsluni.singlepage.web.StockHttpRouteActor.Stock
import org.scalatest.matchers.MustMatchers
import org.scalatest.{BeforeAndAfterAll, WordSpec}

/**
 * Class to test the RepositoryActor
 */
class RepositoryTest extends TestKit(ActorSystem()) with WordSpec with MustMatchers with BeforeAndAfterAll  {

  "A RepositoryActor with InMemoryRepository" must {
    val actorRef = TestActorRef(new RepositoryActor with InMemoryRepository)
    "store the message in the repository" in {
      // This call is synchronous. The actor receive() method will be called in the current thread
      actorRef ! StoreStock(Stock("AAPL",Nil))
      // With actorRef.underlyingActor, we can access the SimpleActor instance created by Akka
      actorRef.underlyingActor.listStocks() must equal(List("AAPL"))

    }
  }

  "A RepositoryActor with TestRepository" must {
    val actorRef = TestActorRef(new RepositoryActor with TestRepository)
    "return the correct result of the TestRepository" in {
      // This call is synchronous. The actor receive() method will be called in the current thread
      actorRef ! ReadStockNames
      // With actorRef.underlyingActor, we can access the SimpleActor instance created by Akka
      actorRef.underlyingActor.listStocks() must equal(List("AAPL","ORCL","MSFT","FB"))

    }
  }

}
