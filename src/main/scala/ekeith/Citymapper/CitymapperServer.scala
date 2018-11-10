package ekeith.Citymapper

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import ekeith.Citymapper.data._
import io.circe.generic.auto._

import scala.concurrent.{ExecutionContext, Future}

trait CitymapperServer {

  def travelTimeRequest(start: Wgs84Coordinate, end: Wgs84Coordinate,
                        timeInfo: Option[TimeRequestInfo] = None, key: CmKey)
                       (implicit ec: ExecutionContext, actorSystem: ActorSystem, materializer: Materializer): Future[TravelTimeResponse]

  def coverageRequest(coord: Wgs84Coordinate, id: Option[String] = None)
                     (implicit ec: ExecutionContext, actorSystem: ActorSystem): Future[CoverageResponse]


  val baseApiUri: String = "https://developer.citymapper.com/api/1/"

  def travelTimeCoordsGenerator(start: Wgs84Coordinate, end: Wgs84Coordinate)(baseUri: String): String =
    baseUri +
    s"traveltime/?startcoord=${start.latitude}%2C${start.longitude}&endcoord=${end.latitude}%2C${end.longitude}"

  def travelTimeBoundsGenerator(timeInfo: Option[TimeRequestInfo])(baseUri: String): String =
    timeInfo match {
      case Some(TimeRequestInfo(time, timeType)) => baseUri + s"&time=$time&time_type=$timeType"
      case None => baseUri
    }

  def addKey(key: CmKey)(baseUri: String): String = baseUri + "&key=" + key

  def travelTimeUriGenerator(start: Wgs84Coordinate, end: Wgs84Coordinate,
                             timeInfo: Option[TimeRequestInfo] = None, key: CmKey): String =
    {
      travelTimeCoordsGenerator(start, end) _ andThen
      travelTimeBoundsGenerator(timeInfo) _ andThen
      addKey(key) _
    }.apply(baseApiUri)

}

object CitymapperOnlineServer extends CitymapperServer with FailFastCirceSupport {

  def travelTimeRequest(start: Wgs84Coordinate, end: Wgs84Coordinate,
                        timeInfo: Option[TimeRequestInfo] = None, key: CmKey)
                       (implicit ec: ExecutionContext, actorSystem: ActorSystem, materializer: Materializer): Future[TravelTimeResponse] = {

    val requestUri = travelTimeUriGenerator(start, end, timeInfo, key)

    Http().singleRequest(HttpRequest(uri = requestUri)).flatMap(Unmarshal(_).to[TravelTimeResponse])
  }

  def coverageRequest(coord: Wgs84Coordinate, id: Option[String] = None)
                     (implicit ec: ExecutionContext, actorSystem: ActorSystem): Future[CoverageResponse] =
    Future.successful(CoverageResponse(Vector(PointCoverage(true, coord, id))))
}


object CitymapperTestServer extends CitymapperServer {

  def travelTimeRequest(start: Wgs84Coordinate, end: Wgs84Coordinate,
                        timeInfo: Option[TimeRequestInfo] = None, key: CmKey)
                       (implicit ec: ExecutionContext, actorSystem: ActorSystem, materializer: Materializer): Future[TravelTimeResponse] =
    Future.successful(TravelTimeResponse(20))

  def coverageRequest(coord: Wgs84Coordinate, id: Option[String] = None)
                     (implicit ec: ExecutionContext, actorSystem: ActorSystem): Future[CoverageResponse] =
    Future.successful(CoverageResponse(Vector(PointCoverage(true, coord, id))))
}

