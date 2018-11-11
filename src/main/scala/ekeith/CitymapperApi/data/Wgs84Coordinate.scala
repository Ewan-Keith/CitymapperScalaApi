package ekeith.CitymapperApi.data

/**
  * Representation of a WGS84 position coordinate.
  *
  * @param latitude The WGS84 latitude of the point.
  * @param longitude The WGS84 longitude of the point.
  */
final case class Wgs84Coordinate(latitude: Double, longitude: Double)