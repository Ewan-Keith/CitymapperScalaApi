package ekeith.CitymapperApi

import com.github.nscala_time.time.Imports.DateTime
import ekeith.CitymapperApi.data.{CmKey, TimeRequestInfo, Wgs84Coordinate, arrival}
import org.scalatest.{AsyncFlatSpec, Matchers}


class UriConstructorsSpec extends AsyncFlatSpec with Matchers {

  // initialize the UriConstructors trait for testing
  object UriConstructorsTest extends UriConstructors

  val testingCoord = Wgs84Coordinate(10.0, 10.0)
  val testingTimeInfo = TimeRequestInfo(new DateTime("2004-12-13T21:39:45.618-08:00"), arrival())
  val testingKey = CmKey("abcd1234")

  "UriConstructors.travelTimeCoordsGenerator()" should "return the correct coords URI" in {
    UriConstructorsTest.travelTimeCoordsGenerator(testingCoord, testingCoord)("abc/") shouldEqual
      "abc/traveltime/?startcoord=10.0%2C10.0&endcoord=10.0%2C10.0"
  }

  "UriConstructors.travelTimeInfoGenerator()" should "return the correct time bounds URI" in {
    UriConstructorsTest.travelTimeInfoGenerator(Some(testingTimeInfo))("abc/") shouldEqual
      "abc/&time=2004-12-14T05:39:45.618Z&time_type=arrival"
  }

  "UriConstructors.travelTimeInfoGenerator()" should "return the base string if no time info provided" in {
    UriConstructorsTest.travelTimeInfoGenerator(None)("abc/") shouldEqual
      "abc/"
  }

  "UriConstructors.addKey()" should "return the correct key added URI" in {
    UriConstructorsTest.addKey(testingKey)("abc/") shouldEqual
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

  "UriConstructors.travelTimeUriGenerator()" should "construct a full travel time request URI" in {
    UriConstructorsTest.travelTimeUriGenerator("https://developer.citymapper.com/api/1/",
      fullTimeTestStart, fullTimeTestEnd, Some(fullTimeTestTimeInfo), fullTimeTestKey) shouldEqual fullTimeTestResult
  }

  "UriConstructors.travelTimeUriGenerator()" should "construct a full travel time request URI (with no time info)" in {
    UriConstructorsTest.travelTimeUriGenerator("https://developer.citymapper.com/api/1/",
      fullTimeTestStart, fullTimeTestEnd, None, fullTimeTestKey) shouldEqual fullTimeTestNoTimeResult
  }

  "UriConstructors.coverageCoordsGenerator()" should "return the correct coords URI" in {
  UriConstructorsTest.coverageCoordsGenerator(testingCoord)("abc/") shouldEqual
    "abc/singlepointcoverage/?coord=10.0%2C10.0"
  }

  val fullCoverageTestResult =
    "https://developer.citymapper.com/api/1/singlepointcoverage/?coord=51.525246%2C0.084672&key=efg"

  "UriConstructors.constructCoverageUri()" should "construct a full coverage request URI" in {
    UriConstructorsTest.coverageUriGenerator("https://developer.citymapper.com/api/1/",
      fullTimeTestStart, fullTimeTestKey) shouldEqual fullCoverageTestResult
  }


}
