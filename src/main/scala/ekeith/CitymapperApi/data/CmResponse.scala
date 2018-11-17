package ekeith.CitymapperApi.data

// provides snake to camelcase json encoding
import ekeith.CitymapperApi.CitymapperOnlineServer.config
import io.circe.{ Decoder, Encoder, HCursor, Json }
import io.circe.generic.extras._
import cats.syntax.either._

/**
  * Abstract Response from the Citymapper API
  *
  * Extended by all concrete Citymapper response objects.
  */
sealed trait CmResponse

/**
  * A single point's CityMapper coverage response.
  *
  * @param covered True if Citymapper covers this location, else False.
  * @param coord The point's WGS84 coordinate.
  * @param id An optional ID that the user can attach to any requested point.
  */
final case class PointCoverage(covered: Boolean, coord: Wgs84Coordinate, id: Option[String] = None)

object PointCoverage {

  implicit val decodeFoo: Decoder[PointCoverage] = new Decoder[PointCoverage] {
    final def apply(c: HCursor): Decoder.Result[PointCoverage] =
      for {
        covered <- c.downField("covered").as[Boolean]
        coord <- c.downField("coord").as[List[Double]]
      } yield {
        PointCoverage(covered, Wgs84Coordinate(coord.head, coord.tail.head), None)
      }
  }

}



/**
  * The response from Citymapper to a coverage request
  * @param points A vector of coverage results for requested points.
  */
final case class CoverageResponse(points: Vector[PointCoverage]) extends CmResponse


/**
  * The response from Citymapper to a travel time request.
  * @param travelTimeMinutes The estimated travel time reported in minutes.
  */
@ConfiguredJsonCodec final case class TravelTimeResponse(travelTimeMinutes: Int) extends CmResponse
