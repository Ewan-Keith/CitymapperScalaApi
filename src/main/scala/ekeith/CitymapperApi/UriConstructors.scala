package ekeith.CitymapperApi

import ekeith.CitymapperApi.data.{CmKey, TimeRequestInfo, Wgs84Coordinate}


/**
  * Defines utility functions for constructing URI addresses.
  *
  * All utility functions for constructing the URIs of CityMapper API addresses are held within this object. They are
  * made available to the Citymapper Server class as the `CitymapperServer` trait extends this one.
  */
trait UriConstructors {

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
  def travelTimeUriGenerator(baseApiUri: String, start: Wgs84Coordinate, end: Wgs84Coordinate,
                             timeInfo: Option[TimeRequestInfo] = None, key: CmKey): String =
    {travelTimeCoordsGenerator(start, end) _ andThen
      travelTimeInfoGenerator(timeInfo) _ andThen
      addKey(key) _}.apply(baseApiUri)


  /**
    * Adds the coverage coordinates to a base API URI.
    *
    * Given a location, this method returns a String => String function that appends the appropriate
    * URI parameters to a base Citymapper URI.
    *
    * @param location The location for which to request coverage information.
    * @return String => String if not fully applied, String if it is.
    */
  def coverageCoordsGenerator(location: Wgs84Coordinate)(baseUri: String): String = baseUri +
    s"singlepointcoverage/?coord=${location.latitude}%2C${location.longitude}"


  /**
    * Constructs a full coverage request URI.
    *
    * Taking all required information for a coverage request (including a suitable base URI) returns
    * a full URI for making the coverage request.
    *
    * @param start The starting location from which to request travel time.
    * @param end The end location for which to request travel time.
    * @param timeInfo An optional TimeRequestInfo object giving an arrival time to use for the request. If left
    *                 as `None` then the current time will be used.
    * @param key A Citymapper key contained in a `CmKey` object.
    * @return String
    */
  def coverageUriGenerator(baseApiUri: String, location: Wgs84Coordinate, key: CmKey): String =
    {coverageCoordsGenerator(location) _ andThen
      addKey(key) _}.apply(baseApiUri)
}

