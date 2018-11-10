package ekeith.Citymapper

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import ekeith.Citymapper.data._
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

  "CitymapperTestServer.coverageRequest()" should "eventually return a true coverage response" in {
    CitymapperTestServer.coverageRequest(testingCoord, Some("123"))
      .map(x => x shouldEqual CoverageResponse(Vector(PointCoverage(true, testingCoord, Some("123")))))
  }

  "CitymapperTestServer.baseApiUri" should "be the correct Citymapper URI base" in {
    CitymapperTestServer.baseApiUri shouldEqual "https://developer.citymapper.com/api/1/"
  }

  "CitymapperOnlineServer.travelTimeCoordsGenerator()" should "return the correct coords URI" in {
    CitymapperOnlineServer.travelTimeCoordsGenerator(testingCoord, testingCoord)("abc/") shouldEqual
      "abc/traveltime/?startcoord=10.0%2C10.0&endcoord=10.0%2C10.0"
  }

  "CitymapperOnlineServer.travelTimeBoundsGenerator()" should "return the correct time bounds URI" in {
    CitymapperOnlineServer.travelTimeBoundsGenerator(Some(testingTimeInfo))("abc/") shouldEqual
      "abc/&time=2004-12-14T05:39:45.618Z&time_type=arrival"
  }

  "CitymapperOnlineServer.travelTimeBoundsGenerator()" should "return the base string if no time info provided" in {
    CitymapperOnlineServer.travelTimeBoundsGenerator(None)("abc/") shouldEqual
      "abc/"
  }

  "CitymapperOnlineServer.addKey()" should "return the correct key added URI" in {
    CitymapperOnlineServer.addKey(testingKey)("abc/") shouldEqual
      "abc/&key=abcd1234"
  }

  val fullTimeTestStart = Wgs84Coordinate(51.525246, 0.084672)
  val fullTimeTestEnd = Wgs84Coordinate(51.559098, 0.074503)
  val fullTimeTestTimeInfo = TimeRequestInfo(new DateTime("2014-11-06T19:00:02z"), arrival())
  val fullTimeTestKey = CmKey("efg")
  val fullTimeTestResult =
    "https://developer.citymapper.com/api/1/traveltime/?startcoord=51.525246%2C0.084672&endcoord=51.559098%2C0.074503&time=2014-11-06T19:00:02.000Z&time_type=arrival&key=efg"
  val fullTimeTestNoTimeResult =
    "https://developer.citymapper.com/api/1/traveltime/?startcoord=51.525246%2C0.084672&endcoord=51.559098%2C0.074503&key=efg"

  "CitymapperOnlineServer.constructTravelTimeUri()" should "construct a full travel time request URI" in {
    CitymapperOnlineServer.travelTimeUriGenerator(
      fullTimeTestStart, fullTimeTestEnd, Some(fullTimeTestTimeInfo), fullTimeTestKey) shouldEqual fullTimeTestResult
  }

  "CitymapperOnlineServer.constructTravelTimeUri()" should "construct a full travel time request URI (with no time info)" in {
    CitymapperOnlineServer.travelTimeUriGenerator(
      fullTimeTestStart, fullTimeTestEnd, None, fullTimeTestKey) shouldEqual fullTimeTestNoTimeResult
  }

}
