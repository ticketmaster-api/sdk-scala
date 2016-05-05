# Ticketmaster API: Discovery Scala SDK

For more detailed information about the API and to get your API key head [here](http://developer.ticketmaster.com/). This SDK supports [v2](http://developer.ticketmaster.com/products-and-docs/apis/discovery/v2/).

## Create API client

```scala
import com.ticketmaster.api.discovery._

val api = DiscoveryApi("your-api-key")
```

API calls are non-blocking so ensure that there is a `scala.concurrent.ExecutionContext` implicitly available.

## Making API calls

API calls all take a case class representing the criteria for that call. For example, search events takes a `com.ticketmaster.api.discovery.SearchEventsRequest` on which you can set your criteria.

```scala
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

val criteria = SearchEventsRequest(keyword = "coachella", size = 5)

val pendingResponse: Future[PageResponse[Events]] = api.searchEvents(criteria)
```

See docs for full list of criteria and resources they relate to.

Rate limit details are provided in every response in `com.ticketmaster.api.discovery.RateLimits`. Details include current rate limit per day, time for next rate limit reset, how many calls remain in current time period and how many calls over the rate limit. Also, API calls for searching will return a `com.ticketmaster.api.discovery.Page` as part of the result.

```scala
pendingResponse.map { response =>
    println(response.pageResult.result.events) // search results
    println(response.pageResult.page) // paging details
    println(response.rateLimits) // rate limits
  }
```

## Error handling

This library piggy-backs on `scala.concurrent.Future` semantics for handling errors. Exceptions can be handled in the `recover` combinator.

## Remaining work

* Expose _links and _embedded for each resource.
* Remove boilerplate code transforming criteria to http request, possibly via Shapeless.

## Wish list

* Allow pluggable http client.
* Allow pluggable json parser.
* Turn search results into a stream/iterator.

## Building
Normal build -> `sbt clean test`
With coverage -> `sbt clean coverage test coverageReport`

