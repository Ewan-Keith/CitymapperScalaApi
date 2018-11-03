package ekeith.Citymapper

import ekeith.Citymapper.data.CoverageRequest

case class CitymapperRepo(coverageRequests: Option[Vector[CoverageRequest]] = None) { self =>

  def checkCoverage(request: CoverageRequest): CitymapperRepo =
    self.coverageRequests match {
      case None => CitymapperRepo(coverageRequests = Some(Vector(request)))
      case Some(vector) => CitymapperRepo(coverageRequests = Some(vector :+ request))
    }

}

object CitymapperRepo {

  def apply(): CitymapperRepo = new CitymapperRepo()

}