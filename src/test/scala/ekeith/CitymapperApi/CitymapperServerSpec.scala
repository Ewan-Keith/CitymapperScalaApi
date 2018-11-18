package ekeith.CitymapperApi

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import ekeith.CitymapperApi.data._
import org.scalatest.{AsyncFlatSpec, Matchers}
import com.github.nscala_time.time.Imports._

class CitymapperServerSpec extends AsyncFlatSpec with Matchers {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val testingCoord = Wgs84Coordinate(10.0, 10.0)
  val testingTimeInfo = TimeRequestInfo(new DateTime("2004-12-13T21:39:45.618-08:00"), arrival())
  val testingKey = CmKey("abcd1234")

  "CitymapperTestServer.travelTimeRequest()" should "eventually return TravelTimeResponse(20)" in {
    CitymapperTestServer.travelTimeRequest(
      testingCoord, testingCoord, None, CmKey("abc")).map(x => x shouldEqual TravelTimeResponse(20)
    )
  }

  "CitymapperTestServer.coverageRequest()" should "eventually return a true PointCoverage response" in {
    CitymapperTestServer.coverageRequest(testingCoord, testingKey)
      .map(x => x shouldEqual PointCoverage(true, testingCoord, None))
  }

  "CitymapperTestServer.baseApiUri" should "be the correct Citymapper URI base" in {
    CitymapperTestServer.baseApiUri shouldEqual "https://developer.citymapper.com/api/1/"
  }

}
