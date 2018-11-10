package ekeith.Citymapper

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.stream.Materializer
import ekeith.Citymapper.data._

import scala.concurrent.{ExecutionContext, Future}

case class CitymapperRepo(coverageRequests: Option[Vector[CoverageRequest]],
                          travelTimeRequests: Option[Vector[TravelTimeRequest]],
                          server: CitymapperServer) { self =>

  def checkCoverage(request: CoverageRequest): CitymapperRepo =
    self.coverageRequests match {
      case None => CitymapperRepo(coverageRequests = Some(Vector(request)),
        travelTimeRequests = self.travelTimeRequests,
        server = self.server)
      case Some(vector) => CitymapperRepo(coverageRequests = Some(vector :+ request),
        travelTimeRequests = self.travelTimeRequests,
        self.server)
    }

  def checkTravelTime(request: TravelTimeRequest): CitymapperRepo =
    self.travelTimeRequests match {
      case None => CitymapperRepo(coverageRequests = self.coverageRequests,
        travelTimeRequests = Some(Vector(request)),
        server = self.server)
      case Some(vector) => CitymapperRepo(coverageRequests = self.coverageRequests,
        travelTimeRequests = Some(vector :+ request),
        server = self.server)
    }

  val requests: Option[Vector[CmRequest]] =
    self.coverageRequests.toVector.flatten ++ self.travelTimeRequests.toVector.flatten match {
      case vec if vec.nonEmpty => Some(vec)
      case vec if vec.isEmpty => None
    }

  def run()(implicit ec: ExecutionContext, actorSystem: ActorSystem, materializer: Materializer, key: CmKey): Option[Vector[Future[CmResponse]]] = requests
    .map(vec =>
      vec.map({
        case TravelTimeRequest(start, end, time) => server.travelTimeRequest(start, end, time, key)
        case CoverageRequest(coord, id) => server.coverageRequest(coord, id)
      })
    )

}

object CitymapperRepo {

  def apply(server: CitymapperServer = CitymapperOnlineServer): CitymapperRepo =
    new CitymapperRepo(coverageRequests = None, travelTimeRequests = None, server = server)
}