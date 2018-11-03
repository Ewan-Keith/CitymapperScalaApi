package ekeith.Citymapper.data

/**
  * Abstract Request for the Citymapper API.
  *
  * Extended by all concrete Citymapper request objects.
  */
sealed trait CmRequest

/**
  * Represents a single coverage requestto be sent to Citymapper.
  * @param coord The WGS84 position to check the coverage of
  * @param id An optional identifier, if provided will be attached to the matching `PointCoverage` response.
  */
final case class CoverageRequest(coord: Wgs84Coordinate, id: Option[String] = None) extends CmRequest
