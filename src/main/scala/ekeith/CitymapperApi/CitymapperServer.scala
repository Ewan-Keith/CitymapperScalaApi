package ekeith.CitymapperApi

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import ekeith.CitymapperApi.data._
import io.circe.generic.auto._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Abstract trait for Citymapper server implementations.
  *
  * In practice the `CitymapperOnlineServer` implementation will be used for all actual server calls. This abstract
  * trait, and the implementation `CitymapperTestServer` primarily exist to enable testing of the `CitymapperRepo`
  * class without having to mock http calls.
  *
  * All methods shared between the test and online implementations are abstractly defined in this trait, http call
  * construction utility methods are mixed in from the `UriConstructors` trait, leaving the implementations to
  * focus purely on their respective implementation details.
  */
trait CitymapperServer extends UriConstructors {

  /**
    * Makes a travel time request against the appropriate Citymapper server backend.
    *
    * @param start The starting location from which to request travel time.
    * @param end The end location for which to request travel time.
    * @param timeInfo An optional TimeRequestInfo object giving an arrival time to use for the request. If left
    *                 as `None` then the current time will be used.
    * @return Future[TravelTimeResponse]
    */
  def travelTimeRequest(start: Wgs84Coordinate, end: Wgs84Coordinate,
                        timeInfo: Option[TimeRequestInfo] = None, key: CmKey)
                       (implicit ec: ExecutionContext, actorSystem: ActorSystem, materializer: Materializer): Future[TravelTimeResponse]

// to be documented once implemented
  def coverageRequest(coord: Wgs84Coordinate, key: CmKey)
                     (implicit ec: ExecutionContext, actorSystem: ActorSystem, materializer: Materializer): Future[PointCoverage]


  /** The base URI of the Citymapper API. */
  val baseApiUri: String = "https://developer.citymapper.com/api/1/"

}

/**
  * Contains information for the online Citymapper API server.
  *
  * Implementing the `CitymapperServer` trait, this object defines the methods to make live requests against the
  * Citymapper API. This is in contrast to the `CitymapperTestServer` which defines matching methods but for offline
  * testing.
  */
object CitymapperOnlineServer extends CitymapperServer with FailFastCirceSupport {

  // defines custom json parsing logic to neatly convert snake to camel case when en/decoding json to Scala objects.
  import io.circe.generic.extras._
  implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames
  import ekeith.CitymapperApi.data.CoverageResponse._

  /**
    * Makes a travel time request against the online Citymapper API server.
    *
    * @param start The starting location from which to request travel time.
    * @param end The end location for which to request travel time.
    * @param timeInfo An optional TimeRequestInfo object giving an arrival time to use for the request. If left
    *                 as `None` then the current time will be used.
    * @return Future[TravelTimeResponse]
    */
  def travelTimeRequest(start: Wgs84Coordinate, end: Wgs84Coordinate,
                        timeInfo: Option[TimeRequestInfo] = None, key: CmKey)
                       (implicit ec: ExecutionContext, actorSystem: ActorSystem, materializer: Materializer): Future[TravelTimeResponse] = {

    val requestUri = travelTimeUriGenerator(baseApiUri, start, end, timeInfo, key)
    Http().singleRequest(HttpRequest(uri = requestUri)).flatMap(Unmarshal(_).to[TravelTimeResponse])
  }

  // to be documented once implemented
  def coverageRequest(coord: Wgs84Coordinate, key: CmKey)
                     (implicit ec: ExecutionContext, actorSystem: ActorSystem, materializer: Materializer): Future[PointCoverage] = {

    val requestUri = coverageUriGenerator(baseApiUri, coord, key)
    Http().singleRequest(HttpRequest(uri = requestUri)).flatMap(Unmarshal(_).to[CoverageResponse]).map(_.points.head)
  }
}

/**
  * Contains methods for the testing Citymapper API 'server'.
  */
object CitymapperTestServer extends CitymapperServer {

  /**
    * Makes a travel time request against the appropriate Citymapper server backend.
    *
    * @param start The starting location from which to request travel time.
    * @param end The end location for which to request travel time.
    * @param timeInfo An optional TimeRequestInfo object giving an arrival time to use for the request. If left
    *                 as `None` then the current time will be used.
    * @return Future[TravelTimeResponse]
    */
  def travelTimeRequest(start: Wgs84Coordinate, end: Wgs84Coordinate,
                        timeInfo: Option[TimeRequestInfo] = None, key: CmKey)
                       (implicit ec: ExecutionContext, actorSystem: ActorSystem, materializer: Materializer): Future[TravelTimeResponse] =
    Future.successful(TravelTimeResponse(20))

    // to be documented once implemented
    def coverageRequest(coord: Wgs84Coordinate, key: CmKey)
                       (implicit ec: ExecutionContext, actorSystem: ActorSystem, materializer: Materializer): Future[PointCoverage] =
    Future.successful(CoverageResponse(Vector(PointCoverage(true, coord, None))).points.head)
}

