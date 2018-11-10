package ekeith.Citymapper.data

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

/**
  * The response from Citymapper to a coverage request
  * @param points A vector of coverage results for requested points.
  */
final case class CoverageResponse(points: Vector[PointCoverage]) extends CmResponse

/**
  * The response from Citymapper to a travel time request.
  * @param travel_time_minutes The estimated travel time reported in minutes.
  */
final case class TravelTimeResponse(travel_time_minutes: Int) extends CmResponse
