package com.ticketmaster.api.discovery

import com.ticketmaster.api.Api._
import com.ticketmaster.api.discovery.domain._
import com.ticketmaster.api.http.protocol.{HttpRequest, HttpResponse}
import com.ticketmaster.api.test.BaseSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class VenuesSpec extends BaseSpec with TestableDiscoveryApi {

  val testApiKey = "12345"

  val responseHeaders = Map("Rate-Limit" -> "5000",
    "Rate-Limit-Available" -> "5000",
    "Rate-Limit-Over" -> "0",
    "Rate-Limit-Reset" -> "1453180594367")

  behavior of "discovery venue API"

  it should "search for a venue by keyword" in {
    val expectedRequest = HttpRequest(root = "https://app.ticketmaster.com/discovery/v2", queryParams = Map("keyword" -> "candlestick", "apikey" -> testApiKey)) / "venues.json"
    val response = HttpResponse(status = 200, headers = responseHeaders, body = Some(VenuesSpec.searchVenuesResponse))
    val api = testableApi(expectedRequest, response)

    val pendingResponse: Future[PageResponse[Venues]] = api.searchVenues(SearchVenuesRequest(keyword = "candlestick"))

    whenReady(pendingResponse) { r =>
      r.pageResult._embedded.venues.size should be(2)
      r.pageResult._embedded.venues.head.name should be("Candlestick Park")
      r.pageResult.page should be(Page(20, 2, 1, 0))
      r.pageResult._links.self should be(Link("/discovery/v2/venues.json{?page,size,sort}", Some(true)))
    }
  }

  it should "get a venue" in {
    val expectedRequest = HttpRequest(root = "https://app.ticketmaster.com/discovery/v2", queryParams = Map("apikey" -> testApiKey)) / "venues" / "KovZpZAalvAA.json"
    val response = HttpResponse(status = 200, headers = responseHeaders, body = Some(VenuesSpec.getVenueResponse))
    val api = testableApi(expectedRequest, response)

    val pendingResponse: Future[Response[Venue]] = api.getVenue(GetVenueRequest("KovZpZAalvAA"))

    whenReady(pendingResponse) { r =>
      r.result.id should be("KovZpZAalvAA")
    }
  }

  it should "throw exception if venue not found" in {
    val expectedRequest = HttpRequest(root = "https://app.ticketmaster.com/discovery/v2", queryParams = Map("apikey" -> testApiKey)) / "venues" / "abcde.json"
    val response = HttpResponse(status = 404, headers = responseHeaders, body = Some(VenuesSpec.error404))
    val api = testableApi(expectedRequest, response)

    val pendingResponse: Future[Response[Venue]] = api.getVenue(GetVenueRequest("abcde"))

    whenReady(pendingResponse.failed) { t =>
      t shouldBe a[ResourceNotFoundException]
      t.getMessage should be("Errors(Vector(Error(DIS1004,Resource not found with provided criteria (locale=en-us, id=abcde),404)))")
    }
  }

  it should "throw exception if sort param invalid" in {
    val expectedRequest = HttpRequest(root = "https://app.ticketmaster.com/discovery/v2", queryParams = Map("sort" -> "asc", "apikey" -> testApiKey)) / "venues.json"
    val response = HttpResponse(status = 400, headers = responseHeaders, body = Some(VenuesSpec.errorInvalidSortParam))
    val api = testableApi(expectedRequest, response)

    val pendingResponse: Future[PageResponse[Venues]] = api.searchVenues(SearchVenuesRequest(sort = "asc"))

    whenReady(pendingResponse.failed) { t =>
      t shouldBe a[ApiException]
      t.getMessage should be("""Errors(Vector(Error(DIS1005,Query param "sort" may take one of these two values only - {'name,asc', 'name,desc'},400)))""")
    }
  }
}

object VenuesSpec {
  val searchVenuesResponse =
    """
      |{
      |	"_links": {
      |		"self": {
      |			"href": "/discovery/v2/venues.json{?page,size,sort}",
      |			"templated": true
      |		}
      |	},
      |	"_embedded": {
      |		"venues": [{
      |			"name": "Candlestick Park",
      |			"type": "venue",
      |			"id": "KovZpZAalvAA",
      |			"test": false,
      |			"locale": "en-us",
      |			"postalCode": "94124",
      |			"timezone": "America/Los_Angeles",
      |			"city": {
      |				"name": "San Francisco"
      |			},
      |			"state": {
      |				"name": "California",
      |				"stateCode": "CA"
      |			},
      |			"country": {
      |				"name": "United States Of America",
      |				"countryCode": "US"
      |			},
      |			"address": {
      |				"line1": "490 Jamestown Ave,"
      |			},
      |			"location": {
      |				"longitude": "-122.38157310",
      |				"latitude": "37.71264660"
      |			},
      |			"markets": [{
      |				"id": "41"
      |			}],
      |			"dmas": [{
      |				"id": 250
      |			}, {
      |				"id": 273
      |			}, {
      |				"id": 282
      |			}, {
      |				"id": 341
      |			}, {
      |				"id": 368
      |			}, {
      |				"id": 374
      |			}, {
      |				"id": 382
      |			}],
      |			"_links": {
      |				"self": {
      |					"href": "/discovery/v2/venues/KovZpZAalvAA?locale=en-us"
      |				}
      |			}
      |		}, {
      |			"name": "The Lot at Candlestick",
      |			"type": "venue",
      |			"id": "KovZpapJ1e",
      |			"test": false,
      |			"locale": "en-us",
      |			"postalCode": "94124",
      |			"timezone": "America/Los_Angeles",
      |			"city": {
      |				"name": "San Francisco"
      |			},
      |			"state": {
      |				"name": "California",
      |				"stateCode": "CA"
      |			},
      |			"country": {
      |				"name": "United States Of America",
      |				"countryCode": "US"
      |			},
      |			"address": {
      |				"line1": "Candlestick Park"
      |			},
      |			"location": {
      |				"longitude": "-122.38515290",
      |				"latitude": "37.71345290"
      |			},
      |			"markets": [{
      |				"id": "41"
      |			}],
      |			"dmas": [{
      |				"id": 250
      |			}, {
      |				"id": 273
      |			}, {
      |				"id": 282
      |			}, {
      |				"id": 341
      |			}, {
      |				"id": 368
      |			}, {
      |				"id": 374
      |			}, {
      |				"id": 382
      |			}],
      |			"_links": {
      |				"self": {
      |					"href": "/discovery/v2/venues/KovZpapJ1e?locale=en-us"
      |				}
      |			}
      |		}]
      |	},
      |	"page": {
      |		"size": 20,
      |		"totalElements": 2,
      |		"totalPages": 1,
      |		"number": 0
      |	}
      |}
    """.stripMargin


  val getVenueResponse =
    """
      |{
      |	"name": "Candlestick Park",
      |	"type": "venue",
      |	"id": "KovZpZAalvAA",
      |	"test": false,
      |	"locale": "en-us",
      |	"postalCode": "94124",
      |	"timezone": "America/Los_Angeles",
      |	"city": {
      |		"name": "San Francisco"
      |	},
      |	"state": {
      |		"name": "California",
      |		"stateCode": "CA"
      |	},
      |	"country": {
      |		"name": "United States Of America",
      |		"countryCode": "US"
      |	},
      |	"address": {
      |		"line1": "490 Jamestown Ave,"
      |	},
      |	"location": {
      |		"longitude": "-122.38157310",
      |		"latitude": "37.71264660"
      |	},
      |	"markets": [{
      |		"id": "41"
      |	}],
      |	"dmas": [{
      |		"id": 250
      |	}, {
      |		"id": 273
      |	}, {
      |		"id": 282
      |	}, {
      |		"id": 341
      |	}, {
      |		"id": 368
      |	}, {
      |		"id": 374
      |	}, {
      |		"id": 382
      |	}],
      |	"_links": {
      |		"self": {
      |			"href": "/discovery/v2/venues/KovZpZAalvAA?locale=en-us"
      |		}
      |	}
      |}
    """.stripMargin

  val error404 =
    """
      |{
      |	"errors": [{
      |		"code": "DIS1004",
      |		"detail": "Resource not found with provided criteria (locale=en-us, id=abcde)",
      |		"status": "404",
      |		"_links": {
      |			"about": {
      |				"href": "/discovery/v2/errors.html#DIS1004"
      |			}
      |		}
      |	}]
      |}
    """.stripMargin

  val errorInvalidSortParam =
    """
      |{
      |	"errors": [{
      |		"code": "DIS1005",
      |		"detail": "Query param \"sort\" may take one of these two values only - {'name,asc', 'name,desc'}",
      |		"status": "400",
      |		"_links": {
      |			"about": {
      |				"href": "/discovery/v2/errors.html#DIS1005"
      |			}
      |		}
      |	}]
      |}
    """.stripMargin
}