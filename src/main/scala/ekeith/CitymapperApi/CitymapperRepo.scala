package ekeith.CitymapperApi

import akka.actor.ActorSystem
import akka.stream.Materializer
import ekeith.CitymapperApi.data._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Citymapper API access class.
  *
  * Represents a collection of queries to be made against the Citymapper API. These queries are simply stored
  * by the class, and are not ran until the `run()` method is called on an instance of the class.
  *
  * @param coverageRequests A vector of coverage requests to be made against the Citymapper API.
  * @param travelTimeRequests A vector of travel time requests to be made against the Citymapper API.
  * @param server Hands in the server details for the Citymapper API. Default value should be used in most
  *               cases, the primary reason for handing this in a a parameter is for testing.
  */
case class CitymapperRepo(coverageRequests: Vector[CoverageRequest],
                          travelTimeRequests: Vector[TravelTimeRequest],
                          server: CitymapperServer) { self =>

  /**
    * Store a coverage check request.
    *
    * Returns a new CityMapperRepo class with the provided coverage request added.
    * @param request A CoverageRequest to be made against the Citymapper API.
    * @return CityMapperRepo
    */
  def checkCoverage(request: CoverageRequest): CitymapperRepo =
    self.coverageRequests match {
      case vec if vec.isEmpty => CitymapperRepo(coverageRequests = Vector(request),
        travelTimeRequests = self.travelTimeRequests, server = self.server)
      case vec if vec.nonEmpty => CitymapperRepo(coverageRequests = vec :+ request,
        travelTimeRequests = self.travelTimeRequests, self.server)
    }

  /**
    * Store a travel time request.
    *
    * Returns a new CityMapperRepo class with the provided travel time request added.
    * @param request A TravelTimeRequest to be made against the Citymapper API.
    * @return CityMapperRepo
    */
  def checkTravelTime(request: TravelTimeRequest): CitymapperRepo =
    self.travelTimeRequests match {
      case vec if vec.isEmpty => CitymapperRepo(coverageRequests = self.coverageRequests,
        travelTimeRequests = Vector(request), server = self.server)
      case vec if vec.nonEmpty => CitymapperRepo(coverageRequests = self.coverageRequests,
        travelTimeRequests = vec :+ request, server = self.server)
    }

  /** The requests that are currently stored by the Repo object. */
  val requests: Vector[CmRequest] =
    self.coverageRequests ++ self.travelTimeRequests match {
      case vec if vec.nonEmpty => vec
      case vec if vec.isEmpty => Vector()
    }

  /**
    * Executes the stored Citymapper API requests.
    *
    * Calling this method will attempt to execute all requests stored by a CitymapperRepo instance against the
    * Citymapper API. No explicit parameters are taken by the method, but the akka execution context, actor system,
    * and materializer are taken as implicit parameters. Also required in the implicit scope is an instance of a
    * CmKey object containing a valid Citymapper API key.
    * @return Vector[ Future[CmResponse] ]
    */
  def run()(implicit ec: ExecutionContext, actorSystem: ActorSystem, materializer: Materializer, key: CmKey): Vector[Future[CmResponse]] =
    requests.map({
        case TravelTimeRequest(start, end, time) => server.travelTimeRequest(start, end, time, key)
        case CoverageRequest(coord, id) => server.coverageRequest(coord, id)
      })
}

/**
  * The CitymapperRepo companion object.
  *
  * Provides a convenience constructor for a new, empty CitymapperRepo instance.
  */
object CitymapperRepo {

  def apply(server: CitymapperServer = CitymapperOnlineServer): CitymapperRepo =
    new CitymapperRepo(coverageRequests = Vector(), travelTimeRequests = Vector(), server = server)
}