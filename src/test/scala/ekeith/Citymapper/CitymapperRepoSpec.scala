package ekeith.Citymapper

import ekeith.Citymapper.data.{CoverageRequest, CoverageResponse, Wgs84Coordinate}
import org.scalatest._


class CitymapperRepoSpec extends FlatSpec with Matchers {

  val covReq = CoverageRequest(coord = Wgs84Coordinate(latitude = 51.578973, longitude = -0.124147))
  val covReq2 = CoverageRequest(coord = Wgs84Coordinate(latitude = 41.84, longitude = -73))

  "Citymapper.checkCoverage(covReq)" should "capture Some[_] request" in {
    CitymapperRepo().checkCoverage(covReq).coverageRequests shouldBe a[Some[_]]
  }

  it should "capture Some[Vector[_]] of requests" in {
    CitymapperRepo().checkCoverage(covReq).coverageRequests.get shouldBe a [Vector[_]]
  }

  it should "capture a Vector of length 1 for a single request" in {
    CitymapperRepo().checkCoverage(covReq).coverageRequests.get should have length 1
  }

  "Citymapper.checkCoverage(covReq).checkCoverage(covReq2)" should "capture a Vector of length 2" in {
    CitymapperRepo().checkCoverage(covReq).checkCoverage(covReq2).coverageRequests.get should have length 2
  }


}

