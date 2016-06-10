package com.ticketmaster.api.discovery

import java.time.{Clock, Instant, ZoneId, ZonedDateTime}

import com.ticketmaster.api.Api._
import com.ticketmaster.api.discovery.domain._
import com.ticketmaster.api.http.protocol.{HttpRequest, HttpResponse}
import com.ticketmaster.api.test.BaseSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class EventsSpec extends BaseSpec with TestableHttpDiscoveryApi {

  val testApiKey = "12345"

  val responseHeaders = Map("Rate-Limit" -> "5000",
    "Rate-Limit-Available" -> "5000",
    "Rate-Limit-Over" -> "0",
    "Rate-Limit-Reset" -> "1453180594367")

  behavior of "discovery event API"

  it should "search for an event by keyword" in {
    val expectedRequest = HttpRequest(root = "https://app.ticketmaster.com/discovery/v2", queryParams = Map("keyword" -> "hollywood", "apikey" -> testApiKey)) / "events.json"
    val response = HttpResponse(status = 200, headers = responseHeaders, body = Some(EventsSpec.searchEventsResponse))
    val api = newHttpDiscoveryApi(expectedRequest, response)

    val pendingResponse: Future[PageResponse[Events]] = api.searchEvents(SearchEventsRequest(keyword = "hollywood"))

    whenReady(pendingResponse) { r =>
      r.pageResult._embedded.events.size should be(1)
      r.pageResult._embedded.events.head.name should be("Murray Celebrity Magician")
      r.pageResult.page should be(Page(1,4254,4254,0))
      r.pageResult._links.self should be(Link("/discovery/v2/events.json?view=null&size=1&keyword=hollywood{&page,sort}", Some(true)))
    }
  }

  it should "search for an event by start date" in {
    val expectedRequest = HttpRequest(root = "https://app.ticketmaster.com/discovery/v2", queryParams = Map("startDateTime" -> "2016-04-20T02:00:00Z", "apikey" -> testApiKey)) / "events.json"
    val response = HttpResponse(status = 200, headers = responseHeaders, body = Some(EventsSpec.searchEventsResponse))
    val api = newHttpDiscoveryApi(expectedRequest, response)

    val searchEventsRequest = SearchEventsRequest(startDateTime = ZonedDateTime.now(Clock.fixed(Instant.ofEpochMilli(1461117600000L), ZoneId.of("UTC"))))
    val pendingResponse: Future[PageResponse[Events]] = api.searchEvents(searchEventsRequest)

    whenReady(pendingResponse) { r =>
      r.pageResult._embedded.events.size should be(1)
      r.pageResult._embedded.events.head.name should be("Murray Celebrity Magician")
      r.pageResult.page should be(Page(1,4254,4254,0))
      r.pageResult._links.self should be(Link("/discovery/v2/events.json?view=null&size=1&keyword=hollywood{&page,sort}", Some(true)))
    }
  }

  it should "get event images" in {
    val expectedRequest = HttpRequest(root = "https://app.ticketmaster.com/discovery/v2", queryParams = Map("apikey" -> testApiKey)) / "events" / "k7vGFfdS_Gp6G" / "images.json"
    val response = HttpResponse(status = 200, headers = responseHeaders, body = Some(EventsSpec.getEventImagesResponse))
    val api = newHttpDiscoveryApi(expectedRequest, response)

    val pendingResponse: Future[Response[EventImages]] = api.getEventImages(GetEventImagesRequest("k7vGFfdS_Gp6G"))

    whenReady(pendingResponse) { r =>
      r.result.`type` should be("event")
      r.result.id should be("k7vGFfdS_Gp6G")
      r.result.images should have length (10)
    }
  }

  it should "get an event" in {
    val expectedRequest = HttpRequest(root = "https://app.ticketmaster.com/discovery/v2", queryParams = Map("apikey" -> testApiKey)) / "events" / "1FC8v88M_eZ75ave.json"
    val response = HttpResponse(status = 200, headers = responseHeaders, body = Some(EventsSpec.getEventResponse))
    val api = newHttpDiscoveryApi(expectedRequest, response)

    val pendingResponse: Future[Response[Event]] = api.getEvent(GetEventRequest("1FC8v88M_eZ75ave"))

    whenReady(pendingResponse) { r =>
      r.result.id should be("1FC8v88M_eZ75ave")
    }
  }

  it should "throw exception if event not found" in {
    val expectedRequest = HttpRequest(root = "https://app.ticketmaster.com/discovery/v2", queryParams = Map("apikey" -> testApiKey)) / "events" / "abcde.json"
    val response = HttpResponse(status = 404, headers = responseHeaders, body = Some(EventsSpec.error404))
    val api = newHttpDiscoveryApi(expectedRequest, response)

    val pendingResponse: Future[Response[Event]] = api.getEvent(GetEventRequest("abcde"))

    whenReady(pendingResponse.failed) { t =>
      t shouldBe a[ResourceNotFoundException]
      t.getMessage should be("Errors(Vector(Error(DIS1004,Resource not found with provided criteria (locale=en-us, id=abcde),404)))")
    }
  }
}

object EventsSpec {
  val searchEventsResponse =
    """
      |{
      |	"_links": {
      |		"self": {
      |			"href": "/discovery/v2/events.json?view=null&size=1&keyword=hollywood{&page,sort}",
      |			"templated": true
      |		},
      |		"next": {
      |			"href": "/discovery/v2/events.json?view=null&keyword=hollywood&page=1&size=1{&sort}",
      |			"templated": true
      |		}
      |	},
      |	"_embedded": {
      |		"events": [{
      |			"name": "Murray Celebrity Magician",
      |			"type": "event",
      |			"id": "1FC8v88M_eZ75ave",
      |			"test": false,
      |			"url": "http://ticketmaster.com/event/390050437642A801",
      |			"locale": "en-us",
      |			"images": [{
      |				"ratio": "16_9",
      |				"url": "http://s1.ticketm.net/dbimages/193738a.jpg",
      |				"width": 205,
      |				"height": 115,
      |				"fallback": false
      |			}, {
      |				"ratio": "4_3",
      |				"url": "http://s1.ticketm.net/dam/c/FAMILY_CUSTOM.jpg",
      |				"width": 305,
      |				"height": 225,
      |				"fallback": true
      |			}, {
      |				"ratio": "3_2",
      |				"url": "http://s1.ticketm.net/dam/c/FAMILY_ARTIST_PAGE_3_2.jpg",
      |				"width": 305,
      |				"height": 203,
      |				"fallback": true
      |			}, {
      |				"ratio": "16_9",
      |				"url": "http://s1.ticketm.net/dam/c/FAMILY_RECOMENDATION_16_9.jpg",
      |				"width": 100,
      |				"height": 56,
      |				"fallback": true
      |			}, {
      |				"ratio": "16_9",
      |				"url": "http://s1.ticketm.net/dam/c/FAMILY_RETINA_LANDSCAPE_16_9.jpg",
      |				"width": 1136,
      |				"height": 639,
      |				"fallback": true
      |			}, {
      |				"ratio": "3_2",
      |				"url": "http://s1.ticketm.net/dam/c/FAMILY_RETINA_PORTRAIT_3_2.jpg",
      |				"width": 640,
      |				"height": 427,
      |				"fallback": true
      |			}, {
      |				"ratio": "3_2",
      |				"url": "http://s1.ticketm.net/dam/c/FAMILY_TABLET_LANDSCAPE_3_2.jpg",
      |				"width": 1024,
      |				"height": 683,
      |				"fallback": true
      |			}, {
      |				"ratio": "16_9",
      |				"url": "http://s1.ticketm.net/dam/c/FAMILY_RETINA_PORTRAIT_16_9.jpg",
      |				"width": 640,
      |				"height": 360,
      |				"fallback": true
      |			}, {
      |				"ratio": "16_9",
      |				"url": "http://s1.ticketm.net/dam/c/FAMILY_TABLET_LANDSCAPE_16_9.jpg",
      |				"width": 1024,
      |				"height": 576,
      |				"fallback": true
      |			}, {
      |				"ratio": "16_9",
      |				"url": "http://s1.ticketm.net/dam/c/FAMILY_TABLET_LANDSCAPE_LARGE_16_9.jpg",
      |				"width": 2048,
      |				"height": 1152,
      |				"fallback": true
      |			}],
      |			"sales": {
      |				"public": {
      |					"startDateTime": "2016-02-03T18:00:00Z",
      |					"startTBD": false,
      |					"endDateTime": "2016-06-09T22:00:00Z"
      |				}
      |			},
      |			"dates": {
      |				"start": {
      |					"localDate": "2016-06-09",
      |					"localTime": "16:00:00",
      |					"dateTime": "2016-06-09T23:00:00Z",
      |					"dateTBD": false,
      |					"dateTBA": false,
      |					"timeTBA": false,
      |					"noSpecificTime": false
      |				},
      |				"timezone": "America/Los_Angeles",
      |				"status": {
      |					"code": "offsale"
      |				}
      |			},
      |			"classifications": [{
      |				"primary": true,
      |				"segment": {
      |					"id": "KZFzniwnSyZfZ7v7na",
      |					"name": "Arts & Theatre"
      |				},
      |				"genre": {
      |					"id": "KnvZfZ7v7lv",
      |					"name": "Magic & Illusion"
      |				},
      |				"subGenre": {
      |					"id": "KZazBEonSMnZfZ7v7l7",
      |					"name": "Magic"
      |				}
      |			}],
      |			"promoter": {
      |				"id": "494"
      |			},
      |			"info": "MURRAY Celebrity Magician featured on the History Channel's Pawn Stars and who dazzled the judges and 22 million people as a finalist on Americas Got Talent; who you have also seen on Access Hollywood LIVE and many more. He is currently on his newest TV series on Reelz Channel Extreme Escapes. This year, Murray has received the Hollywood F.A.M.E. award for Career Achievement in the Magical Arts, Best Comedy Variety Show of the Year by the Los Angeles Comedy Festival, and Comedy Show of the Year Award by the Fans of Entertainment Hall of Fame. Murray's show is great for the whole family and kids of any age where Murray ties in his comedy with spectacular illusions. You will also meet Murray's sidekick and guest act Lefty who will surprise you with his own brand of magic and his beautiful assistant Chloe!!! After the show you will get to meet Murray and his cast for photos and autographs as he loves meeting his audience! Meet and Greet is included with all ticket prices!",
      |			"pleaseNote": "GROUPS Orders of 10 or more should be referred to the Group Sales department at 1-866-574-3851 or email EntertainmentGroupSales@Caesars.com",
      |			"priceRanges": [{
      |				"type": "standard",
      |				"currency": "USD",
      |				"min": 35.28,
      |				"max": 45.37
      |			}],
      |			"_links": {
      |				"self": {
      |					"href": "/discovery/v2/events/1FC8v88M_eZ75ave?locale=en-us"
      |				},
      |				"attractions": [{
      |					"href": "/discovery/v2/attractions/K8vZ917KdCV?locale=en-us"
      |				}],
      |				"venues": [{
      |					"href": "/discovery/v2/venues/KovZpZAJalJA?locale=en-us"
      |				}]
      |			},
      |			"_embedded": {
      |				"venues": [{
      |					"name": "Sin City at Planet Hollywood Resort & Casino",
      |					"type": "venue",
      |					"id": "KovZpZAJalJA",
      |					"test": false,
      |					"url": "http://ticketmaster.com/venue/467880",
      |					"locale": "en-us",
      |					"postalCode": "89109",
      |					"timezone": "America/Los_Angeles",
      |					"city": {
      |						"name": "Las Vegas"
      |					},
      |					"state": {
      |						"name": "Nevada",
      |						"stateCode": "NV"
      |					},
      |					"country": {
      |						"name": "United States Of America",
      |						"countryCode": "US"
      |					},
      |					"address": {
      |						"line1": "3667 Las Vegas Blvd S"
      |					},
      |					"location": {
      |						"longitude": "-115.17197706",
      |						"latitude": "36.10959955"
      |					},
      |					"markets": [{
      |						"id": "14"
      |					}],
      |					"dmas": [{
      |						"id": 319
      |					}],
      |					"_links": {
      |						"self": {
      |							"href": "/discovery/v2/venues/KovZpZAJalJA?locale=en-us"
      |						}
      |					}
      |				}],
      |				"attractions": [{
      |					"name": "Murray Sawchuck",
      |					"type": "attraction",
      |					"id": "K8vZ917KdCV",
      |					"test": false,
      |					"url": "http://ticketmaster.com/artist/2068753",
      |					"locale": "en-us",
      |					"images": [{
      |						"ratio": "16_9",
      |						"url": "http://s1.ticketm.net/dam/c/ARTS_RECOMENDATION_16_9.jpg",
      |						"width": 100,
      |						"height": 56,
      |						"fallback": true
      |					}, {
      |						"ratio": "4_3",
      |						"url": "https://s1.ticketm.net/dbimages/210011a.jpg",
      |						"width": 305,
      |						"height": 225,
      |						"fallback": false
      |					}, {
      |						"ratio": "16_9",
      |						"url": "http://s1.ticketm.net/dam/c/ARTS_TABLET_LANDSCAPE_LARGE_16_9.jpg",
      |						"width": 2048,
      |						"height": 1152,
      |						"fallback": true
      |					}, {
      |						"ratio": "16_9",
      |						"url": "http://s1.ticketm.net/dam/c/ARTS_RETINA_PORTRAIT_16_9.jpg",
      |						"width": 640,
      |						"height": 360,
      |						"fallback": true
      |					}, {
      |						"ratio": "16_9",
      |						"url": "https://s1.ticketm.net/dbimages/210012a.jpg",
      |						"width": 205,
      |						"height": 115,
      |						"fallback": false
      |					}, {
      |						"ratio": "16_9",
      |						"url": "http://s1.ticketm.net/dam/c/ARTS_RETINA_LANDSCAPE_16_9.jpg",
      |						"width": 1136,
      |						"height": 639,
      |						"fallback": true
      |					}, {
      |						"ratio": "3_2",
      |						"url": "http://s1.ticketm.net/dam/c/ARTS_RETINA_PORTRAIT_3_2.jpg",
      |						"width": 640,
      |						"height": 427,
      |						"fallback": true
      |					}, {
      |						"ratio": "3_2",
      |						"url": "http://s1.ticketm.net/dam/c/ARTS_TABLET_LANDSCAPE_3_2.jpg",
      |						"width": 1024,
      |						"height": 683,
      |						"fallback": true
      |					}, {
      |						"ratio": "16_9",
      |						"url": "http://s1.ticketm.net/dam/c/ARTS_TABLET_LANDSCAPE_16_9.jpg",
      |						"width": 1024,
      |						"height": 576,
      |						"fallback": true
      |					}, {
      |						"ratio": "3_2",
      |						"url": "http://s1.ticketm.net/dam/c/ARTS_ARTIST_PAGE_3_2.jpg",
      |						"width": 305,
      |						"height": 203,
      |						"fallback": true
      |					}],
      |					"classifications": [{
      |						"primary": true,
      |						"segment": {
      |							"id": "KZFzniwnSyZfZ7v7na",
      |							"name": "Arts & Theatre"
      |						},
      |						"genre": {
      |							"id": "KnvZfZ7v7lv",
      |							"name": "Magic & Illusion"
      |						},
      |						"subGenre": {
      |							"id": "KZazBEonSMnZfZ7v7l7",
      |							"name": "Magic"
      |						}
      |					}],
      |					"_links": {
      |						"self": {
      |							"href": "/discovery/v2/attractions/K8vZ917KdCV?locale=en-us"
      |						}
      |					}
      |				}]
      |			}
      |		}]
      |	},
      |	"page": {
      |		"size": 1,
      |		"totalElements": 4254,
      |		"totalPages": 4254,
      |		"number": 0
      |	}
      |}
    """.stripMargin


  val getEventImagesResponse =
    """
      |{
      |	"type": "event",
      |	"id": "k7vGFfdS_Gp6G",
      |	"images": [{
      |		"ratio": "16_9",
      |		"url": "http://s1.ticketm.net/dam/c/MISC_RECOMENDATION_16_9.jpg",
      |		"width": 100,
      |		"height": 56,
      |		"fallback": true
      |	}, {
      |		"ratio": "16_9",
      |		"url": "http://s1.ticketm.net/dam/c/MISC_RETINA_PORTRAIT_16_9.jpg",
      |		"width": 640,
      |		"height": 360,
      |		"fallback": true
      |	}, {
      |		"ratio": "3_2",
      |		"url": "http://s1.ticketm.net/dam/c/MISC_RETINA_PORTRAIT_3_2.jpg",
      |		"width": 640,
      |		"height": 427,
      |		"fallback": true
      |	}, {
      |		"ratio": "3_2",
      |		"url": "http://s1.ticketm.net/dam/c/MISC_ARTIST_PAGE_3_2.jpg",
      |		"width": 305,
      |		"height": 203,
      |		"fallback": true
      |	}, {
      |		"ratio": "16_9",
      |		"url": "http://s1.ticketm.net/dam/c/MISC_TABLET_LANDSCAPE_16_9.jpg",
      |		"width": 1024,
      |		"height": 576,
      |		"fallback": true
      |	}, {
      |		"ratio": "16_9",
      |		"url": "http://s1.ticketm.net/dam/c/MISC_RETINA_LANDSCAPE_16_9.jpg",
      |		"width": 1136,
      |		"height": 639,
      |		"fallback": true
      |	}, {
      |		"ratio": "4_3",
      |		"url": "http://s1.ticketm.net/dam/c/MISC_CUSTOM.jpg",
      |		"width": 305,
      |		"height": 225,
      |		"fallback": true
      |	}, {
      |		"ratio": "16_9",
      |		"url": "http://s1.ticketm.net/dam/c/MISC_TABLET_LANDSCAPE_LARGE_16_9.jpg",
      |		"width": 2048,
      |		"height": 1152,
      |		"fallback": true
      |	}, {
      |		"ratio": "16_9",
      |		"url": "http://s1.ticketm.net/dbimages/234299a.png",
      |		"width": 205,
      |		"height": 115,
      |		"fallback": false
      |	}, {
      |		"ratio": "3_2",
      |		"url": "http://s1.ticketm.net/dam/c/MISC_TABLET_LANDSCAPE_3_2.jpg",
      |		"width": 1024,
      |		"height": 683,
      |		"fallback": true
      |	}],
      |	"_links": {
      |		"self": {
      |			"href": "/discovery/v2/events/k7vGFfdS_Gp6G/images?locale=en-us"
      |		}
      |	}
      |}
    """.stripMargin

  val getEventResponse =
    """
      |{
      |	"name": "Murray Celebrity Magician",
      |	"type": "event",
      |	"id": "1FC8v88M_eZ75ave",
      |	"test": false,
      |	"url": "http://ticketmaster.com/event/390050437642A801",
      |	"locale": "en-us",
      |	"images": [{
      |		"ratio": "16_9",
      |		"url": "http://s1.ticketm.net/dbimages/193738a.jpg",
      |		"width": 205,
      |		"height": 115,
      |		"fallback": false
      |	}, {
      |		"ratio": "4_3",
      |		"url": "http://s1.ticketm.net/dam/c/FAMILY_CUSTOM.jpg",
      |		"width": 305,
      |		"height": 225,
      |		"fallback": true
      |	}, {
      |		"ratio": "3_2",
      |		"url": "http://s1.ticketm.net/dam/c/FAMILY_ARTIST_PAGE_3_2.jpg",
      |		"width": 305,
      |		"height": 203,
      |		"fallback": true
      |	}, {
      |		"ratio": "16_9",
      |		"url": "http://s1.ticketm.net/dam/c/FAMILY_RECOMENDATION_16_9.jpg",
      |		"width": 100,
      |		"height": 56,
      |		"fallback": true
      |	}, {
      |		"ratio": "16_9",
      |		"url": "http://s1.ticketm.net/dam/c/FAMILY_RETINA_LANDSCAPE_16_9.jpg",
      |		"width": 1136,
      |		"height": 639,
      |		"fallback": true
      |	}, {
      |		"ratio": "3_2",
      |		"url": "http://s1.ticketm.net/dam/c/FAMILY_RETINA_PORTRAIT_3_2.jpg",
      |		"width": 640,
      |		"height": 427,
      |		"fallback": true
      |	}, {
      |		"ratio": "3_2",
      |		"url": "http://s1.ticketm.net/dam/c/FAMILY_TABLET_LANDSCAPE_3_2.jpg",
      |		"width": 1024,
      |		"height": 683,
      |		"fallback": true
      |	}, {
      |		"ratio": "16_9",
      |		"url": "http://s1.ticketm.net/dam/c/FAMILY_RETINA_PORTRAIT_16_9.jpg",
      |		"width": 640,
      |		"height": 360,
      |		"fallback": true
      |	}, {
      |		"ratio": "16_9",
      |		"url": "http://s1.ticketm.net/dam/c/FAMILY_TABLET_LANDSCAPE_16_9.jpg",
      |		"width": 1024,
      |		"height": 576,
      |		"fallback": true
      |	}, {
      |		"ratio": "16_9",
      |		"url": "http://s1.ticketm.net/dam/c/FAMILY_TABLET_LANDSCAPE_LARGE_16_9.jpg",
      |		"width": 2048,
      |		"height": 1152,
      |		"fallback": true
      |	}],
      |	"sales": {
      |		"public": {
      |			"startDateTime": "2016-02-03T18:00:00Z",
      |			"startTBD": false,
      |			"endDateTime": "2016-06-09T22:00:00Z"
      |		}
      |	},
      |	"dates": {
      |		"start": {
      |			"localDate": "2016-06-09",
      |			"localTime": "16:00:00",
      |			"dateTime": "2016-06-09T23:00:00Z",
      |			"dateTBD": false,
      |			"dateTBA": false,
      |			"timeTBA": false,
      |			"noSpecificTime": false
      |		},
      |		"timezone": "America/Los_Angeles",
      |		"status": {
      |			"code": "offsale"
      |		}
      |	},
      |	"classifications": [{
      |		"primary": true,
      |		"segment": {
      |			"id": "KZFzniwnSyZfZ7v7na",
      |			"name": "Arts & Theatre"
      |		},
      |		"genre": {
      |			"id": "KnvZfZ7v7lv",
      |			"name": "Magic & Illusion"
      |		},
      |		"subGenre": {
      |			"id": "KZazBEonSMnZfZ7v7l7",
      |			"name": "Magic"
      |		}
      |	}],
      |	"promoter": {
      |		"id": "494"
      |	},
      |	"info": "MURRAY Celebrity Magician featured on the History Channel's Pawn Stars and who dazzled the judges and 22 million people as a finalist on Americas Got Talent; who you have also seen on Access Hollywood LIVE and many more. He is currently on his newest TV series on Reelz Channel Extreme Escapes. This year, Murray has received the Hollywood F.A.M.E. award for Career Achievement in the Magical Arts, Best Comedy Variety Show of the Year by the Los Angeles Comedy Festival, and Comedy Show of the Year Award by the Fans of Entertainment Hall of Fame. Murray's show is great for the whole family and kids of any age where Murray ties in his comedy with spectacular illusions. You will also meet Murray's sidekick and guest act Lefty who will surprise you with his own brand of magic and his beautiful assistant Chloe!!! After the show you will get to meet Murray and his cast for photos and autographs as he loves meeting his audience! Meet and Greet is included with all ticket prices!",
      |	"pleaseNote": "GROUPS Orders of 10 or more should be referred to the Group Sales department at 1-866-574-3851 or email EntertainmentGroupSales@Caesars.com",
      |	"priceRanges": [{
      |		"type": "standard",
      |		"currency": "USD",
      |		"min": 35.28,
      |		"max": 45.37
      |	}],
      |	"_links": {
      |		"self": {
      |			"href": "/discovery/v2/events/1FC8v88M_eZ75ave?locale=en-us"
      |		},
      |		"attractions": [{
      |			"href": "/discovery/v2/attractions/K8vZ917KdCV?locale=en-us"
      |		}],
      |		"venues": [{
      |			"href": "/discovery/v2/venues/KovZpZAJalJA?locale=en-us"
      |		}]
      |	},
      |	"_embedded": {
      |		"venues": [{
      |			"name": "Sin City at Planet Hollywood Resort & Casino",
      |			"type": "venue",
      |			"id": "KovZpZAJalJA",
      |			"test": false,
      |			"url": "http://ticketmaster.com/venue/467880",
      |			"locale": "en-us",
      |			"postalCode": "89109",
      |			"timezone": "America/Los_Angeles",
      |			"city": {
      |				"name": "Las Vegas"
      |			},
      |			"state": {
      |				"name": "Nevada",
      |				"stateCode": "NV"
      |			},
      |			"country": {
      |				"name": "United States Of America",
      |				"countryCode": "US"
      |			},
      |			"address": {
      |				"line1": "3667 Las Vegas Blvd S"
      |			},
      |			"location": {
      |				"longitude": "-115.17197706",
      |				"latitude": "36.10959955"
      |			},
      |			"markets": [{
      |				"id": "14"
      |			}],
      |			"dmas": [{
      |				"id": 319
      |			}],
      |			"_links": {
      |				"self": {
      |					"href": "/discovery/v2/venues/KovZpZAJalJA?locale=en-us"
      |				}
      |			}
      |		}],
      |		"attractions": [{
      |			"name": "Murray Sawchuck",
      |			"type": "attraction",
      |			"id": "K8vZ917KdCV",
      |			"test": false,
      |			"url": "http://ticketmaster.com/artist/2068753",
      |			"locale": "en-us",
      |			"images": [{
      |				"ratio": "16_9",
      |				"url": "http://s1.ticketm.net/dam/c/ARTS_RECOMENDATION_16_9.jpg",
      |				"width": 100,
      |				"height": 56,
      |				"fallback": true
      |			}, {
      |				"ratio": "4_3",
      |				"url": "https://s1.ticketm.net/dbimages/210011a.jpg",
      |				"width": 305,
      |				"height": 225,
      |				"fallback": false
      |			}, {
      |				"ratio": "16_9",
      |				"url": "http://s1.ticketm.net/dam/c/ARTS_TABLET_LANDSCAPE_LARGE_16_9.jpg",
      |				"width": 2048,
      |				"height": 1152,
      |				"fallback": true
      |			}, {
      |				"ratio": "16_9",
      |				"url": "http://s1.ticketm.net/dam/c/ARTS_RETINA_PORTRAIT_16_9.jpg",
      |				"width": 640,
      |				"height": 360,
      |				"fallback": true
      |			}, {
      |				"ratio": "16_9",
      |				"url": "https://s1.ticketm.net/dbimages/210012a.jpg",
      |				"width": 205,
      |				"height": 115,
      |				"fallback": false
      |			}, {
      |				"ratio": "16_9",
      |				"url": "http://s1.ticketm.net/dam/c/ARTS_RETINA_LANDSCAPE_16_9.jpg",
      |				"width": 1136,
      |				"height": 639,
      |				"fallback": true
      |			}, {
      |				"ratio": "3_2",
      |				"url": "http://s1.ticketm.net/dam/c/ARTS_RETINA_PORTRAIT_3_2.jpg",
      |				"width": 640,
      |				"height": 427,
      |				"fallback": true
      |			}, {
      |				"ratio": "3_2",
      |				"url": "http://s1.ticketm.net/dam/c/ARTS_TABLET_LANDSCAPE_3_2.jpg",
      |				"width": 1024,
      |				"height": 683,
      |				"fallback": true
      |			}, {
      |				"ratio": "16_9",
      |				"url": "http://s1.ticketm.net/dam/c/ARTS_TABLET_LANDSCAPE_16_9.jpg",
      |				"width": 1024,
      |				"height": 576,
      |				"fallback": true
      |			}, {
      |				"ratio": "3_2",
      |				"url": "http://s1.ticketm.net/dam/c/ARTS_ARTIST_PAGE_3_2.jpg",
      |				"width": 305,
      |				"height": 203,
      |				"fallback": true
      |			}],
      |			"classifications": [{
      |				"primary": true,
      |				"segment": {
      |					"id": "KZFzniwnSyZfZ7v7na",
      |					"name": "Arts & Theatre"
      |				},
      |				"genre": {
      |					"id": "KnvZfZ7v7lv",
      |					"name": "Magic & Illusion"
      |				},
      |				"subGenre": {
      |					"id": "KZazBEonSMnZfZ7v7l7",
      |					"name": "Magic"
      |				}
      |			}],
      |			"_links": {
      |				"self": {
      |					"href": "/discovery/v2/attractions/K8vZ917KdCV?locale=en-us"
      |				}
      |			}
      |		}]
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