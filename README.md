Scala Citymapper API wrapper
=======

A small Scala wrapper over the [Citymapper public API](https://citymapper.com/tools/1063/api-for-robots), providing a
simple set of abstractions for interacting with the API in Scala.

Documentation
-------------

The Cityampper API supports 2 types of requests:

* Coverage requests - returning true or false for a given location indicating whether or not the location is covered by
Citymapper at the time of the request.
* TravelTime requests - returns the estimated number of minutes it will take to travel from one location to another.
By default travel time is calculated starting at the time of the request, but it can also be requested for a given
arrival time.

Both of these requests can be made using this wrapper. All requests are represented by objects under 
`ekeith.CitymapperApi.Data._`. These are then provided to the `CitymapperRepo()` object, allowing for multiple requests
to be chained together in a single object. These requests will only be executed when the `run()` method is called on the
`CitymapperRepo` instance.

Below is an end to end example, checking whether Citymapper covers two locations, and then requesting the travel time
between these points.

```
// import dependencies
//-----------------------------------------------------------------------
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.nscala_time.time.Imports.DateTime
import ekeith.CitymapperApi.CitymapperRepo
import ekeith.CitymapperApi.data._

import scala.concurrent.ExecutionContext
import scala.util.Success

// initialise implicit context for Akka-http running the requests
//-----------------------------------------------------------------------
implicit val system: ActorSystem = ActorSystem()
implicit val materializer: ActorMaterializer = ActorMaterializer()
implicit val executionContext: ExecutionContext = system.dispatcher

// set up objects defining the queries to be made
//-----------------------------------------------------------------------
val startLocation = Wgs84Coordinate(51.525246, 0.084672)
val endLocation = Wgs84Coordinate(51.559098, 0.074503)
val arrivalTime = TimeRequestInfo(new DateTime("2014-11-06T19:00:02z"), arrival())

val cRequest1 = CoverageRequest(startLocation)
val cRequest2 = CoverageRequest(endLocation)
val ttRequest = TravelTimeRequest(startLocation, endLocation, Some(arrivalTime))

// set up API key and run the requests using the `CitymapperRepo` object.
//-----------------------------------------------------------------------

implicit val fullTimeTestKey: CmKey = CmKey(<KEY-STRING>)
val ttResult = CitymapperRepo().checkCoverage(cRequest1).checkCoverage(cRequest2).checkTravelTime(ttRequest).run()

// set up API key and run the requests using the `CitymapperRepo` object.
//-----------------------------------------------------------------------
ttResult.foreach(_.onComplete {case Success(value) => println(value)})
// TravelTimeResponse(38)
// PointCoverage(true,Wgs84Coordinate(51.559098,0.074503),None)
// PointCoverage(true,Wgs84Coordinate(51.525246,0.084672),None)
```