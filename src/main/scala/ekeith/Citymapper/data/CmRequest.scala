package ekeith.Citymapper.data

import com.github.nscala_time.time.Imports._

/**
  * Abstract Request for the Citymapper API.
  *
  * Extended by all concrete Citymapper request objects.
  */
sealed trait CmRequest

/**
  * Represents a single coverage request to be sent to the Citymapper API.
  *
  * @param coord The WGS84 position to check the coverage of
  * @param id An optional identifier, if provided will be attached to the matching `PointCoverage` response.
  */
final case class CoverageRequest(coord: Wgs84Coordinate, id: Option[String] = None) extends CmRequest


/**
  * Represents a single travel time request to be sent to the Citymapper API.
  *
  * @param start The starting location from which request travel time.
  * @param end The end location for which to request travel time.
  * @param time A date & time in ISO-8601 format. If omitted, travel time is
  *             computed for travel at the time of the request.
  * @param timeType If 'time' is provided, a 'time_type' must be provided as well.
  *                 At the moment, the only defined 'time_type' value is 'arrival()'
  */
final case class TravelTimeRequest(start: Wgs84Coordinate, end: Wgs84Coordinate,
                                   timeInfo: Option[TimeRequestInfo] = None) extends CmRequest

final case class TimeRequestInfo(time: DateTime, timeType: TimeType = arrival())