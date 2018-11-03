package ekeith.Citymapper.data

/**
  * Abstract Response from the Citymapper API
  *
  * Extended by all concrete Citymapper response objects.
  */
sealed trait CmResponse

/**
  * Representation of a WGS84 position coordinate.
  *
  * @param latitude The WGS84 latitude of the point.
  * @param longitude The WGS84 longitude of the point.
  */
final case class Wgs84Coordinate(latitude: Double, longitude: Double)

/**
  * A single point's CityMapper coverage response.
  *
  * @param covered True if Citymapper covers this location, else False.
  * @param coord The point's WGS84 coordinate.
  * @param id An optional ID that the user can attach to any requested point.
  */
final case class PointCoverage(covered: Boolean, coord: Wgs84Coordinate, id: Option[String] = None)

/**
  * The response from Citymapper to a coverage request
  * @param points A vector of coverage results for requested points.
  */
final case class CoverageResponse(points: Vector[PointCoverage]) extends CmResponse

/**
  * The response from Citymapper to a travel time request.
  * @param minutes The estimated travel time reported in minutes.
  */
final case class TravelTime(minutes: Int) extends CmResponse
