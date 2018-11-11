package ekeith.CitymapperApi.data

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
  * @param start The starting location from which to request travel time.
  * @param end The end location for which to request travel time.
  * @param timeInfo An optional TimeRequestInfo object giving an arrival time to use for the request. If left
  *                 as `None` then the current time will be used.
  */
final case class TravelTimeRequest(start: Wgs84Coordinate, end: Wgs84Coordinate,
                                   timeInfo: Option[TimeRequestInfo] = None) extends CmRequest

/**
  *
  * @param time A date & time in ISO-8601 format.
  * @param timeType If 'time' is provided, a 'time_type' must be provided as well.
  *                   At the moment, the only defined 'time_type' value is 'arrival()'
  */
final case class TimeRequestInfo(time: DateTime, timeType: TimeType = arrival())