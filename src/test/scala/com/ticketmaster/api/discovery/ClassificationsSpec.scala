package com.ticketmaster.api.discovery

import java.time.ZonedDateTime

import com.ticketmaster.api.discovery.domain._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

class ClassificationsSpec extends ApiSpec with HttpDsl {

  override implicit val patienceConfig = PatienceConfig(2 seconds, 200 millis)

  val apiKey = "12345"

  val responseHeaders = Map("Rate-Limit" -> "5000",
    "Rate-Limit-Available" -> "5000",
    "Rate-Limit-Over" -> "0",
    "Rate-Limit-Reset" -> "1453180594367")

  behavior of "discovery classification API"

  it should "search for all classifications" in {
    val expectedUrl = s"https://app.ticketmaster.com/discovery/v2/classifications.json?apikey=${apiKey}"
    val request = requestMatcher(expectedUrl)

    val response = mockResponse withStatus 200 withHeaders responseHeaders withBody ClassificationsSpec.searchClassificationsResponse

    val http = mockHttp expects request returns response

    val api = new DefaultDiscoveryApi(apiKey, http)
    val pendingResponse: Future[PageResponse[Classifications]] = api.searchClassifications(SearchClassificationsRequest())

    whenReady(pendingResponse) { r =>
      r.pageResult.result.classifications.size should be(6)
      r.pageResult.result.classifications.head.segment.name should be("Arts & Theatre")
      r.pageResult.page should be(Page(20, 6, 1, 0))
      r.pageResult.links.self should be(Link("/discovery/v2/classifications.json{?page,size,sort}", Some(true)))
      r.rateLimits should be(RateLimits(5000, 5000, 0, ZonedDateTime.parse("2016-01-19T05:16:34.367Z[UTC]")))
    }
  }

  it should "get a classification" in {
    val expectedUrl = s"https://app.ticketmaster.com/discovery/v2/classifications/KZFzniwnSyZfZ7v7nE.json?apikey=${apiKey}"
    val request = requestMatcher(expectedUrl)

    val response = mockResponse withStatus 200 withHeaders responseHeaders withBody ClassificationsSpec.getClassificationResponse

    val http = mockHttp expects request returns response

    val api = new DefaultDiscoveryApi(apiKey, http)
    val pendingResponse: Future[Response[Classification]] = api.getClassification(GetClassificationRequest("KZFzniwnSyZfZ7v7nE"))

    whenReady(pendingResponse) { r =>
      r.result.segment.id should be("KZFzniwnSyZfZ7v7nE")
    }
  }

  it should "throw exception if classification not found" in {
    val response = mockResponse withStatus 404 withBody ClassificationsSpec.error404

    val http = mockHttp expects anything returns response

    val api = new DefaultDiscoveryApi(apiKey, http)
    val pendingResponse: Future[Response[Classification]] = api.getClassification(GetClassificationRequest("12345"))

    whenReady(pendingResponse.failed) { t =>
      t shouldBe a[ResourceNotFoundException]
      t.getMessage should be("Resource not found with provided criteria (locale=en-us, id=12345)")
    }
  }
}

object ClassificationsSpec {
  val searchClassificationsResponse =
    """
      |{
      |	"_links": {
      |		"self": {
      |			"href": "/discovery/v2/classifications.json{?page,size,sort}",
      |			"templated": true
      |		}
      |	},
      |	"_embedded": {
      |		"classifications": [{
      |			"segment": {
      |				"id": "KZFzniwnSyZfZ7v7na",
      |				"name": "Arts & Theatre"
      |			},
      |			"_links": {
      |				"self": {
      |					"href": "/discovery/v2/classifications/KZFzniwnSyZfZ7v7na?locale=en-us"
      |				}
      |			}
      |		}, {
      |			"segment": {
      |				"id": "KZFzniwnSyZfZ7v7nn",
      |				"name": "Film"
      |			},
      |			"_links": {
      |				"self": {
      |					"href": "/discovery/v2/classifications/KZFzniwnSyZfZ7v7nn?locale=en-us"
      |				}
      |			}
      |		}, {
      |			"segment": {
      |				"id": "KZFzniwnSyZfZ7v7n1",
      |				"name": "Miscellaneous"
      |			},
      |			"_links": {
      |				"self": {
      |					"href": "/discovery/v2/classifications/KZFzniwnSyZfZ7v7n1?locale=en-us"
      |				}
      |			}
      |		}, {
      |			"segment": {
      |				"id": "KZFzniwnSyZfZ7v7nJ",
      |				"name": "Music"
      |			},
      |			"_links": {
      |				"self": {
      |					"href": "/discovery/v2/classifications/KZFzniwnSyZfZ7v7nJ?locale=en-us"
      |				}
      |			}
      |		}, {
      |			"segment": {
      |				"id": "KZFzniwnSyZfZ7v7nE",
      |				"name": "Sports"
      |			},
      |			"_links": {
      |				"self": {
      |					"href": "/discovery/v2/classifications/KZFzniwnSyZfZ7v7nE?locale=en-us"
      |				}
      |			}
      |		}, {
      |			"segment": {
      |				"id": "KZFzniwnSyZfZ7v7nl",
      |				"name": "Undefined"
      |			},
      |			"_links": {
      |				"self": {
      |					"href": "/discovery/v2/classifications/KZFzniwnSyZfZ7v7nl?locale=en-us"
      |				}
      |			}
      |		}]
      |	},
      |	"page": {
      |		"size": 20,
      |		"totalElements": 6,
      |		"totalPages": 1,
      |		"number": 0
      |	}
      |}
    """.stripMargin


  val getClassificationResponse =
    """
      |{
      |	"segment": {
      |		"id": "KZFzniwnSyZfZ7v7nE",
      |		"name": "Sports"
      |	},
      |	"_links": {
      |		"self": {
      |			"href": "/discovery/v2/classifications/KZFzniwnSyZfZ7v7nE?locale=en-us"
      |		}
      |	}
      |}
    """.stripMargin

  val error404 =
    """
      |{
      |	"errors": [{
      |		"code": "DIS1004",
      |		"detail": "Resource not found with provided criteria (locale=en-us, id=12345)",
      |		"status": "404",
      |		"_links": {
      |			"about": {
      |				"href": "/discovery/v2/errors.html#DIS1004"
      |			}
      |		}
      |	}]
      |}
    """.stripMargin
}