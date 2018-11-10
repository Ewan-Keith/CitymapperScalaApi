package ekeith.Citymapper

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import ekeith.Citymapper.data._
import org.scalatest._
import com.github.nscala_time.time.Imports._

import scala.concurrent.{ExecutionContext, Future}

class CitymapperRepoSpec extends AsyncFlatSpec with Matchers {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val CmTestKey = CmKey("abcd")
  val testServer: CitymapperServer = CitymapperTestServer
  //implicit val executionContext: ExecutionContext = system.dispatcher

  "CitymapperRepo()" should "return an empty CitymapperRepo class" in {
    CitymapperRepo() shouldBe a [CitymapperRepo]
  }

  val covReq = CoverageRequest(coord = Wgs84Coordinate(latitude = 51.578973, longitude = -0.124147))
  val covReq2 = CoverageRequest(coord = Wgs84Coordinate(latitude = 41.84, longitude = -73))
  val timeReq = TravelTimeRequest(start = Wgs84Coordinate(51.525246, 0.084672),
    end = Wgs84Coordinate(51.559098,0.074503),
    timeInfo = Some(TimeRequestInfo(new DateTime("2004-12-13T21:39:45.618-08:00"), arrival()))
  )
  val timeReq2 = TravelTimeRequest(start = Wgs84Coordinate(51.525248, 0.084670),
    end = Wgs84Coordinate(51.559096,0.074505),
    timeInfo = Some(TimeRequestInfo(new DateTime("2009-12-13T21:39:45.618-08:10"), arrival()))
  )

  "CitymapperRepo().checkCoverage(covReq)" should "capture Some[_] request" in {
    CitymapperRepo().checkCoverage(covReq).coverageRequests shouldBe a[Some[_]]
  }

  it should "capture Some[Vector[_]] of requests" in {
    CitymapperRepo().checkCoverage(covReq).coverageRequests.get shouldBe a [Vector[_]]
  }

  it should "capture a Vector of length 1 for a single request" in {
    CitymapperRepo().checkCoverage(covReq).coverageRequests.get should have length 1
  }

  it should "correctly record the CoverageRequests" in {
    CitymapperRepo().checkCoverage(covReq).coverageRequests.get.head should equal (covReq)
  }

  it should "not alter any existing travelTimeRequests" in {
    val capturedTimeReq = CitymapperRepo().checkTravelTime(timeReq)
    capturedTimeReq.travelTimeRequests.get.head should equal (timeReq)
    val capturedCoverageReq = capturedTimeReq.checkCoverage(covReq)
    capturedCoverageReq.travelTimeRequests.get.head should equal (timeReq)
  }

  "CitymapperRepo().checkCoverage(covReq).checkCoverage(covReq2)" should "capture a Vector of length 2" in {
    CitymapperRepo().checkCoverage(covReq).checkCoverage(covReq2).coverageRequests.get should have length 2
  }

  it should "correctly record both of the CoverageRequest" in {
    val reqs = CitymapperRepo().checkCoverage(covReq).checkCoverage(covReq2).coverageRequests.get
    reqs.head should equal (covReq)
    reqs.tail.head should equal (covReq2)
  }


  "CitymapperRepo().checkTravelTime(timeReq)" should "capture Some[_] request" in {
    CitymapperRepo().checkTravelTime(timeReq).travelTimeRequests shouldBe a [Some[_]]
  }

  it should "capture Some[Vector[_]] of requests" in {
    CitymapperRepo().checkTravelTime(timeReq).travelTimeRequests.get shouldBe a [Vector[_]]
  }

  it should "capture a Vector of length 1 for a single request" in {
    CitymapperRepo().checkTravelTime(timeReq).travelTimeRequests.get should have length 1
  }

  it should "correctly record the travelTimeRequest" in {
    CitymapperRepo().checkTravelTime(timeReq).travelTimeRequests.get.head should equal (timeReq)
  }

  it should "not alter any existing coverageRequests" in {
    val capturedCoverageReq = CitymapperRepo().checkCoverage(covReq)
    capturedCoverageReq.coverageRequests.get.head should equal (covReq)
    val capturedTimeReq = capturedCoverageReq.checkTravelTime(timeReq)
    capturedTimeReq.coverageRequests.get.head should equal (covReq)
  }

  "CitymapperRepo().checkTravelTime(timeReq).checkTravelTime(timeReq2)" should "capture a Vector of length 2" in {
    CitymapperRepo().checkTravelTime(timeReq).checkTravelTime(timeReq2).travelTimeRequests.get should have length 2
  }

  it should "correctly record both of the travelTimeRequests" in {
    val reqs = CitymapperRepo().checkTravelTime(timeReq).checkTravelTime(timeReq2).travelTimeRequests.get
    reqs.head should equal (timeReq)
    reqs.tail.head should equal (timeReq2)
  }

  "CitymapperRepo().requests" should "return None if no requests have been specified" in {
    CitymapperRepo().requests shouldBe None
  }

  it should "expose coverageRequests as Some[_]" in {
    CitymapperRepo().checkCoverage(covReq).requests shouldBe a [Some[_]]
  }

  it should "expose travelTimeRequests as Some[_]" in {
    CitymapperRepo().checkTravelTime(timeReq).requests shouldBe a [Some[_]]
  }

  it should "expose coverageRequests as Some[Vector[_]]" in {
    CitymapperRepo().checkCoverage(covReq).requests.get shouldBe a [Vector[_]]
  }

  it should "expose travelTimeRequests as Some[Vector[_]]" in {
    CitymapperRepo().checkTravelTime(timeReq).requests.get shouldBe a [Vector[_]]
  }

  it should "capture a Vector of length 1 for a single coverage request" in {
    CitymapperRepo().checkCoverage(covReq).requests.get should have length 1
  }

  it should "capture a Vector of length 1 for a single travel time request" in {
    CitymapperRepo().checkTravelTime(timeReq).requests.get should have length 1
  }

  it should "capture a Vector of length 2 for a single travel time and coverage request" in {
    CitymapperRepo().checkCoverage(covReq).checkTravelTime(timeReq).requests.get should have length 2
  }

  it should "correctly record both a travelTimeRequest and coverageRequest" in {
    CitymapperRepo().checkCoverage(covReq).checkTravelTime(timeReq).requests.get.head should equal (covReq)
    CitymapperRepo().checkCoverage(covReq).checkTravelTime(timeReq).requests.get.tail.head should equal (timeReq)
  }

  "CitymapperRepo(testServer).run()" should "return None if no requests have been specified" in {
    CitymapperRepo(testServer).run() shouldBe None
  }

  it should "return Some[_] if requests have been specified" in {
    CitymapperRepo(testServer).checkCoverage(covReq).run() shouldBe a [Some[_]]
  }

  it should "return Some[Vector[_]] if requests have been specified" in {
    CitymapperRepo(testServer).checkCoverage(covReq).run().get shouldBe a [Vector[_]]
  }

  it should "return a Vector of length 1 for a single coverage request" in {
    CitymapperRepo(testServer).checkCoverage(covReq).run().get should have length 1
  }

  it should "return a Vector of length 1 for a single travel time request" in {
    CitymapperRepo(testServer).checkTravelTime(timeReq).run().get should have length 1
  }

  it should "capture a Vector of length 2 for a single travel time and coverage request" in {
    CitymapperRepo(testServer).checkCoverage(covReq).checkTravelTime(timeReq).run().get should have length 2
  }

  it should "return Some[Vector[Future[_]]] if requests have been specified" in {
    CitymapperRepo(testServer).checkCoverage(covReq).run().get.head shouldBe a [Future[_]]
  }

  it should "eventually return Some[Vector[Future[CmResponse]]] if requests have been specified" in {
    CitymapperRepo(testServer).checkCoverage(covReq).run().get.head.map(x => x shouldBe a [CmResponse])
  }

  it should "eventually return a CoverageResponse if coverage request specified" in {
    CitymapperRepo(testServer).checkCoverage(covReq).run().get.head.map(x => x shouldBe a [CoverageResponse])
  }

  it should "eventually return a TravelTimeResponse if travelTime request specified" in {
    CitymapperRepo(testServer).checkTravelTime(timeReq).run().get.head.map(x => x shouldBe a [TravelTimeResponse])
  }

}
