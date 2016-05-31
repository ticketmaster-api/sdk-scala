# Ticketmaster API: SDK for Scala

[![Build Status](https://travis-ci.org/ticketmaster-api/sdk-scala.svg?branch=master)](https://travis-ci.org/ticketmaster-api/sdk-scala) [![Coverage Status](https://coveralls.io/repos/github/ticketmaster-api/sdk-scala/badge.svg?branch=master)](https://coveralls.io/github/ticketmaster-api/sdk-scala?branch=master) [ ![Download](https://api.bintray.com/packages/ticketmaster-api/maven/discovery-scala/images/download.svg) ](https://bintray.com/ticketmaster-api/maven/discovery-scala/_latestVersion)

For more detailed information about the API and to get your API key head [here](http://developer.ticketmaster.com/). This SDK supports Discovery [v2](http://developer.ticketmaster.com/products-and-docs/apis/discovery/v2/).

## Dependency

```scala
libraryDependencies ++= Seq(
  "com.ticketmaster.api" %% "discovery-scala" % "0.1.0"
)
```

## Usage

```scala
import com.ticketmaster.api.discovery._

val api = Discovery("your-api-key")
```

API calls are non-blocking so require that there is a `scala.concurrent.ExecutionContext` implicitly available.

All take a case class representing the criteria for that call. For example, search events takes a `com.ticketmaster.api.discovery.SearchEventsRequest` on which you can set your criteria.

```scala
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.ticketmaster.api.discovery.domain._
import com.ticketmaster.api.Api._

val criteria = SearchEventsRequest(keyword = "coachella", size = 5)

val pendingResponse: Future[PageResponse[Events]] = api.searchEvents(criteria)
```

See docs for full list of criteria and resources they relate to.

Rate limit details are provided in every response in `com.ticketmaster.api.discovery.RateLimits`. Search API calls will return paging details in `com.ticketmaster.api.discovery.Page` as part of the result.

## Error handling

This library piggy-backs on `scala.concurrent.Future` semantics for handling errors. Exceptions can be handled in the `recover` combinator.

## Http client

The default http client is [dispatch](https://github.com/dispatch/reboot). It is possible to use a different http client by extending `com.ticketmaster.api.discovery.http.Http` and overriding method `http` in your own instance of `com.ticketmaster.api.discovery.HttpDiscoveryApi`.

## Remaining work

* Expose _links and _embedded for each resource.
* Remove boilerplate code transforming criteria to http request.

## Wish list

* Allow pluggable json parser. Currently using [argonaut](argonaut.io).
* Turn search results into a stream/iterator.

## Build notes

This `README.md` is generated by [tut](https://github.com/tpolecat/tut). Make changes to `src/main/tut/README.md` and run `sbt tut` to generate this file.
