package com.ticketmaster.api.discovery

import com.ticketmaster.api.Api._
import com.ticketmaster.api.discovery.domain._
import com.ticketmaster.api.http.protocol.{HttpRequest, HttpResponse}
import com.ticketmaster.api.test.BaseSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class ClassificationsSpec extends BaseSpec with TestableHttpDiscoveryApi {

  val testApiKey = "12345"

  val responseHeaders = Map("Rate-Limit" -> "5000",
    "Rate-Limit-Available" -> "5000",
    "Rate-Limit-Over" -> "0",
    "Rate-Limit-Reset" -> "1453180594367")

  behavior of "discovery classification API"

  it should "search for all classifications" in {
    val expectedRequest = HttpRequest(root = "https://app.ticketmaster.com/discovery/v2", queryParams = Map("apikey" -> testApiKey)) / "classifications.json"
    val response = HttpResponse(status = 200, headers = responseHeaders, body = Some(ClassificationsSpec.searchClassificationsResponse))
    val api = newHttpDiscoveryApi(expectedRequest, response)

    val pendingResponse: Future[PageResponse[Classifications]] = api.searchClassifications(SearchClassificationsRequest())

    whenReady(pendingResponse) { r =>
      r.pageResult._embedded.classifications.size should be(6)
      r.pageResult._embedded.classifications.head.segment.name should be("Arts & Theatre")
      r.pageResult.page should be(Page(20, 6, 1, 0))
      r.pageResult._links.self should be(Link("/discovery/v2/classifications.json{?page,size,sort}", Some(true)))
    }
  }

  it should "get a classification" in {
    val expectedRequest = HttpRequest(root = "https://app.ticketmaster.com/discovery/v2", queryParams = Map("apikey" -> testApiKey)) / "classifications" / "KZFzniwnSyZfZ7v7nE.json"
    val response = HttpResponse(status = 200, headers = responseHeaders, body = Some(ClassificationsSpec.getClassificationResponse))
    val api = newHttpDiscoveryApi(expectedRequest, response)

    val pendingResponse: Future[Response[Classification]] = api.getClassification(GetClassificationRequest("KZFzniwnSyZfZ7v7nE"))

    whenReady(pendingResponse) { r =>
      r.result.segment.id should be("KZFzniwnSyZfZ7v7nE")
    }
  }

  it should "throw exception if classification not found" in {
    val expectedRequest = HttpRequest(root = "https://app.ticketmaster.com/discovery/v2", queryParams = Map("apikey" -> testApiKey)) / "classifications" / "abcde.json"
    val response = HttpResponse(status = 404, headers = responseHeaders, body = Some(ClassificationsSpec.error404))
    val api = newHttpDiscoveryApi(expectedRequest, response)

    val pendingResponse: Future[Response[Classification]] = api.getClassification(GetClassificationRequest("abcde"))

    whenReady(pendingResponse.failed) { t =>
      t shouldBe a[ResourceNotFoundException]
      t.getMessage should be("Errors(Vector(Error(DIS1004,Resource not found with provided criteria (locale=en-us, id=abcde),404)))")
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
}