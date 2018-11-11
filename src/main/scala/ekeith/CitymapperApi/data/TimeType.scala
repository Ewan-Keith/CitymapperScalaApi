package ekeith.CitymapperApi.data

/**
  * The type of time provided in a travel time request.
  *
  * At the moment, the only value defined by the Citymapper
  * API is 'arrival', which computes the travel time for
  * arriving at the destination at the given time.
  */
sealed trait TimeType

final case class arrival() extends TimeType {
  override def toString: String = "arrival"
}
