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
  * All methods shared between the test and online implementations are abstractly defined in this trait, along with
  * any http call construction utility methods, leaving the implementations to focus purely on their respective
  * implementation details.
  */
trait CitymapperServer {

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
  def coverageRequest(coord: Wgs84Coordinate, id: Option[String] = None)
                     (implicit ec: ExecutionContext, actorSystem: ActorSystem): Future[CoverageResponse]


  /** The base URI of the Citymapper API. */
  val baseApiUri: String = "https://developer.citymapper.com/api/1/"

  /**
    * Adds the travel time coordinates to a base API URI.
    *
    * Given a start and end location, this method returns a String => String function that appends the appropriate
    * URI parameters to a base Citymapper URI.
    *
    * @param start The starting location from which to request travel time.
    * @param end The end location for which to request travel time.
    * @return String => String if not fully applied, String if it is.
    */
  def travelTimeCoordsGenerator(start: Wgs84Coordinate, end: Wgs84Coordinate)(baseUri: String): String = baseUri +
    s"traveltime/?startcoord=${start.latitude}%2C${start.longitude}&endcoord=${end.latitude}%2C${end.longitude}"

  /**
    * Adds the travel time info to a base API URI.
    *
    * Given an optional TimeInfo instance, this method returns a String => String function that appends the appropriate
    * URI parameters to a base Citymapper URI.
    *
    * @param timeInfo An optional TimeRequestInfo object giving an arrival time to use for the request. If left
    *                 as `None` then the current time will be used.
    * @return String => String if not fully applied, String if it is.
    */
  def travelTimeInfoGenerator(timeInfo: Option[TimeRequestInfo])(baseUri: String): String =
    timeInfo match {
      case Some(TimeRequestInfo(time, timeType)) => baseUri + s"&time=$time&time_type=$timeType"
      case None => baseUri
    }

  /**
    * Adds a Citymapper API key to a base API URI.
    *
    * @param key A Citymapper key contained in a `CmKey` object.
    * @return String => String if not fully applied, String if it is.
    */
  def addKey(key: CmKey)(baseUri: String): String = baseUri + "&key=" + key

  /**
    * Constructs a full travel time request URI.
    *
    * Taking all required information for a travel time request (including a suitable base URI) returns
    * a full URI for making the travel time request.
    *
    * @param start The starting location from which to request travel time.
    * @param end The end location for which to request travel time.
    * @param timeInfo An optional TimeRequestInfo object giving an arrival time to use for the request. If left
    *                 as `None` then the current time will be used.
    * @param key A Citymapper key contained in a `CmKey` object.
    * @return String
    */
  def travelTimeUriGenerator(start: Wgs84Coordinate, end: Wgs84Coordinate,
                             timeInfo: Option[TimeRequestInfo] = None, key: CmKey): String =
    {travelTimeCoordsGenerator(start, end) _ andThen
      travelTimeInfoGenerator(timeInfo) _ andThen
      addKey(key) _}.apply(baseApiUri)
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

    val requestUri = travelTimeUriGenerator(start, end, timeInfo, key)
    Http().singleRequest(HttpRequest(uri = requestUri)).flatMap(Unmarshal(_).to[TravelTimeResponse])
  }

  // to be documented once implemented
  def coverageRequest(coord: Wgs84Coordinate, id: Option[String] = None)
                     (implicit ec: ExecutionContext, actorSystem: ActorSystem): Future[CoverageResponse] =
    Future.successful(CoverageResponse(Vector(PointCoverage(true, coord, id))))
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

  def coverageRequest(coord: Wgs84Coordinate, id: Option[String] = None)
                     (implicit ec: ExecutionContext, actorSystem: ActorSystem): Future[CoverageResponse] =
    Future.successful(CoverageResponse(Vector(PointCoverage(true, coord, id))))
}

