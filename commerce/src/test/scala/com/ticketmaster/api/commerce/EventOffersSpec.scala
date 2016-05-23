package com.ticketmaster.api.commerce

import com.ticketmaster.api.Api.Response
import com.ticketmaster.api.commerce.domain.EventOffers
import com.ticketmaster.api.http.protocol.{HttpRequest, HttpResponse}
import com.ticketmaster.api.test.BaseSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class EventOffersSpec extends BaseSpec with TestableCommerceApi {
  override implicit val patienceConfig = PatienceConfig(2 seconds, 200 millis)

  val testApiKey = "12345"

  val responseHeaders = Map("Rate-Limit" -> "5000",
    "Rate-Limit-Available" -> "5000",
    "Rate-Limit-Over" -> "0",
    "Rate-Limit-Reset" -> "1453180594367")

  behavior of "commerce event offers API"

  it should "search for an event by keyword" in {
    val expectedRequest = HttpRequest(root = "https://app.ticketmaster.com/commerce/v2", queryParams = Map("apikey" -> testApiKey)) / "events" / "0000503AC36E4726" / "offers.json"
    val response = HttpResponse(status = 200, headers = responseHeaders, body = Some(EventOffersSpec.getEventOfferResponse))
    val api = testableApi(expectedRequest, response)

    val pendingResponse: Future[Response[EventOffers]] = api.getEventOffers(GetEventOffersRequest("0000503AC36E4726"))

    whenReady(pendingResponse) { r =>
      r.result.limits.max should be(8)
    }
  }
}

object EventOffersSpec {
  val getEventOfferResponse =
    """
      |{
      |	"limits": {
      |		"max": 8
      |	},
      |	"prices": {
      |		"_embedded": [{
      |			"type": "offered-prices",
      |			"attributes": {
      |				"currency": "USD",
      |				"value": "24.50"
      |			},
      |			"relationships": {
      |				"offers": {
      |					"_embedded": [{
      |						"id": "000000000001",
      |						"type": "offers"
      |					}, {
      |						"id": "00000E160006",
      |						"type": "offers"
      |					}]
      |				},
      |				"priceZones": {
      |					"_embedded": [{
      |						"id": "8",
      |						"type": "price-zones"
      |					}]
      |				},
      |				"areas": {
      |					"_embedded": [{
      |						"id": "2",
      |						"type": "areas"
      |					}]
      |				}
      |			}
      |		}, {
      |			"type": "offered-prices",
      |			"attributes": {
      |				"currency": "USD",
      |				"value": "44.50"
      |			},
      |			"relationships": {
      |				"offers": {
      |					"_embedded": [{
      |						"id": "000000000001",
      |						"type": "offers"
      |					}, {
      |						"id": "00000E160006",
      |						"type": "offers"
      |					}]
      |				},
      |				"priceZones": {
      |					"_embedded": [{
      |						"id": "7",
      |						"type": "price-zones"
      |					}]
      |				},
      |				"areas": {
      |					"_embedded": [{
      |						"id": "2",
      |						"type": "areas"
      |					}, {
      |						"id": "11",
      |						"type": "areas"
      |					}]
      |				}
      |			}
      |		}, {
      |			"type": "offered-prices",
      |			"attributes": {
      |				"currency": "USD",
      |				"value": "74.50"
      |			},
      |			"relationships": {
      |				"offers": {
      |					"_embedded": [{
      |						"id": "000000000001",
      |						"type": "offers"
      |					}, {
      |						"id": "00000E160006",
      |						"type": "offers"
      |					}]
      |				},
      |				"priceZones": {
      |					"_embedded": [{
      |						"id": "5",
      |						"type": "price-zones"
      |					}]
      |				},
      |				"areas": {
      |					"_embedded": [{
      |						"id": "1",
      |						"type": "areas"
      |					}, {
      |						"id": "2",
      |						"type": "areas"
      |					}, {
      |						"id": "11",
      |						"type": "areas"
      |					}]
      |				}
      |			}
      |		}, {
      |			"type": "offered-prices",
      |			"attributes": {
      |				"currency": "USD",
      |				"value": "124.50"
      |			},
      |			"relationships": {
      |				"offers": {
      |					"_embedded": [{
      |						"id": "000000000001",
      |						"type": "offers"
      |					}, {
      |						"id": "00000E160006",
      |						"type": "offers"
      |					}]
      |				},
      |				"priceZones": {
      |					"_embedded": [{
      |						"id": "4",
      |						"type": "price-zones"
      |					}]
      |				},
      |				"areas": {
      |					"_embedded": [{
      |						"id": "0",
      |						"type": "areas"
      |					}, {
      |						"id": "1",
      |						"type": "areas"
      |					}, {
      |						"id": "11",
      |						"type": "areas"
      |					}]
      |				}
      |			}
      |		}, {
      |			"type": "offered-prices",
      |			"attributes": {
      |				"currency": "USD",
      |				"value": "174.50"
      |			},
      |			"relationships": {
      |				"offers": {
      |					"_embedded": [{
      |						"id": "000000000001",
      |						"type": "offers"
      |					}, {
      |						"id": "00000E160006",
      |						"type": "offers"
      |					}]
      |				},
      |				"priceZones": {
      |					"_embedded": [{
      |						"id": "3",
      |						"type": "price-zones"
      |					}]
      |				},
      |				"areas": {
      |					"_embedded": [{
      |						"id": "0",
      |						"type": "areas"
      |					}, {
      |						"id": "11",
      |						"type": "areas"
      |					}, {
      |						"id": "13",
      |						"type": "areas"
      |					}]
      |				}
      |			}
      |		}, {
      |			"type": "offered-prices",
      |			"attributes": {
      |				"currency": "USD",
      |				"value": "490.00"
      |			},
      |			"relationships": {
      |				"offers": {
      |					"_embedded": [{
      |						"id": "0000121B0009",
      |						"type": "offers"
      |					}]
      |				},
      |				"priceZones": {
      |					"_embedded": [{
      |						"id": "7",
      |						"type": "price-zones"
      |					}, {
      |						"id": "3",
      |						"type": "price-zones"
      |					}, {
      |						"id": "8",
      |						"type": "price-zones"
      |					}, {
      |						"id": "4",
      |						"type": "price-zones"
      |					}, {
      |						"id": "5",
      |						"type": "price-zones"
      |					}]
      |				}
      |			}
      |		}, {
      |			"type": "offered-prices",
      |			"attributes": {
      |				"currency": "USD",
      |				"value": "720.00"
      |			},
      |			"relationships": {
      |				"offers": {
      |					"_embedded": [{
      |						"id": "0000101A0009",
      |						"type": "offers"
      |					}]
      |				},
      |				"priceZones": {
      |					"_embedded": [{
      |						"id": "7",
      |						"type": "price-zones"
      |					}, {
      |						"id": "3",
      |						"type": "price-zones"
      |					}, {
      |						"id": "8",
      |						"type": "price-zones"
      |					}, {
      |						"id": "4",
      |						"type": "price-zones"
      |					}, {
      |						"id": "5",
      |						"type": "price-zones"
      |					}]
      |				}
      |			}
      |		}]
      |	},
      |	"priceZones": {
      |		"_embedded": [{
      |			"id": "7",
      |			"type": "price-zones",
      |			"attributes": {
      |				"currency": "USD",
      |				"name": "Price Level 4"
      |			},
      |			"relationships": {
      |				"offers": {
      |					"_embedded": [{
      |						"id": "000000000001",
      |						"type": "offers"
      |					}, {
      |						"id": "00000E160006",
      |						"type": "offers"
      |					}, {
      |						"id": "0000101A0009",
      |						"type": "offers"
      |					}, {
      |						"id": "0000121B0009",
      |						"type": "offers"
      |					}]
      |				},
      |				"areas": {
      |					"_embedded": [{
      |						"id": "2",
      |						"type": "areas"
      |					}, {
      |						"id": "11",
      |						"type": "areas"
      |					}]
      |				}
      |			}
      |		}, {
      |			"id": "3",
      |			"type": "price-zones",
      |			"attributes": {
      |				"currency": "USD",
      |				"name": "Price Level 1"
      |			},
      |			"relationships": {
      |				"offers": {
      |					"_embedded": [{
      |						"id": "000000000001",
      |						"type": "offers"
      |					}, {
      |						"id": "00000E160006",
      |						"type": "offers"
      |					}, {
      |						"id": "0000101A0009",
      |						"type": "offers"
      |					}, {
      |						"id": "0000121B0009",
      |						"type": "offers"
      |					}]
      |				},
      |				"areas": {
      |					"_embedded": [{
      |						"id": "0",
      |						"type": "areas"
      |					}, {
      |						"id": "11",
      |						"type": "areas"
      |					}, {
      |						"id": "13",
      |						"type": "areas"
      |					}]
      |				}
      |			}
      |		}, {
      |			"id": "8",
      |			"type": "price-zones",
      |			"attributes": {
      |				"currency": "USD",
      |				"name": "Price Level 5"
      |			},
      |			"relationships": {
      |				"offers": {
      |					"_embedded": [{
      |						"id": "000000000001",
      |						"type": "offers"
      |					}, {
      |						"id": "00000E160006",
      |						"type": "offers"
      |					}, {
      |						"id": "0000101A0009",
      |						"type": "offers"
      |					}, {
      |						"id": "0000121B0009",
      |						"type": "offers"
      |					}]
      |				},
      |				"areas": {
      |					"_embedded": [{
      |						"id": "2",
      |						"type": "areas"
      |					}]
      |				}
      |			}
      |		}, {
      |			"id": "4",
      |			"type": "price-zones",
      |			"attributes": {
      |				"currency": "USD",
      |				"name": "Price Level 2"
      |			},
      |			"relationships": {
      |				"offers": {
      |					"_embedded": [{
      |						"id": "000000000001",
      |						"type": "offers"
      |					}, {
      |						"id": "00000E160006",
      |						"type": "offers"
      |					}, {
      |						"id": "0000101A0009",
      |						"type": "offers"
      |					}, {
      |						"id": "0000121B0009",
      |						"type": "offers"
      |					}]
      |				},
      |				"areas": {
      |					"_embedded": [{
      |						"id": "0",
      |						"type": "areas"
      |					}, {
      |						"id": "1",
      |						"type": "areas"
      |					}, {
      |						"id": "11",
      |						"type": "areas"
      |					}]
      |				}
      |			}
      |		}, {
      |			"id": "5",
      |			"type": "price-zones",
      |			"attributes": {
      |				"currency": "USD",
      |				"name": "Price Level 3"
      |			},
      |			"relationships": {
      |				"offers": {
      |					"_embedded": [{
      |						"id": "000000000001",
      |						"type": "offers"
      |					}, {
      |						"id": "00000E160006",
      |						"type": "offers"
      |					}, {
      |						"id": "0000101A0009",
      |						"type": "offers"
      |					}, {
      |						"id": "0000121B0009",
      |						"type": "offers"
      |					}]
      |				},
      |				"areas": {
      |					"_embedded": [{
      |						"id": "1",
      |						"type": "areas"
      |					}, {
      |						"id": "2",
      |						"type": "areas"
      |					}, {
      |						"id": "11",
      |						"type": "areas"
      |					}]
      |				}
      |			}
      |		}]
      |	},
      |	"areas": {
      |		"_embedded": [{
      |			"id": "2",
      |			"type": "areas",
      |			"attributes": {
      |				"rank": 0,
      |				"name": "CON3",
      |				"areaType": "AREA"
      |			},
      |			"relationships": {
      |				"areas": {
      |					"_embedded": [{
      |						"id": "11",
      |						"type": "areas"
      |					}]
      |				},
      |				"offers": {
      |					"_embedded": [{
      |						"id": "000000000001",
      |						"type": "offers"
      |					}, {
      |						"id": "00000E160006",
      |						"type": "offers"
      |					}, {
      |						"id": "0000101A0009",
      |						"type": "offers"
      |					}, {
      |						"id": "0000121B0009",
      |						"type": "offers"
      |					}]
      |				},
      |				"priceZones": {
      |					"_embedded": [{
      |						"id": "5",
      |						"type": "price-zones"
      |					}, {
      |						"id": "7",
      |						"type": "price-zones"
      |					}, {
      |						"id": "8",
      |						"type": "price-zones"
      |					}]
      |				}
      |			}
      |		}, {
      |			"id": "1",
      |			"type": "areas",
      |			"attributes": {
      |				"rank": 1,
      |				"name": "CON2",
      |				"areaType": "AREA"
      |			},
      |			"relationships": {
      |				"areas": {
      |					"_embedded": [{
      |						"id": "11",
      |						"type": "areas"
      |					}]
      |				},
      |				"offers": {
      |					"_embedded": [{
      |						"id": "000000000001",
      |						"type": "offers"
      |					}, {
      |						"id": "00000E160006",
      |						"type": "offers"
      |					}, {
      |						"id": "0000101A0009",
      |						"type": "offers"
      |					}, {
      |						"id": "0000121B0009",
      |						"type": "offers"
      |					}]
      |				},
      |				"priceZones": {
      |					"_embedded": [{
      |						"id": "4",
      |						"type": "price-zones"
      |					}, {
      |						"id": "5",
      |						"type": "price-zones"
      |					}]
      |				}
      |			}
      |		}, {
      |			"id": "0",
      |			"type": "areas",
      |			"attributes": {
      |				"rank": 2,
      |				"name": "CON1",
      |				"areaType": "AREA"
      |			},
      |			"relationships": {
      |				"areas": {
      |					"_embedded": [{
      |						"id": "11",
      |						"type": "areas"
      |					}]
      |				},
      |				"offers": {
      |					"_embedded": [{
      |						"id": "000000000001",
      |						"type": "offers"
      |					}, {
      |						"id": "00000E160006",
      |						"type": "offers"
      |					}, {
      |						"id": "0000101A0009",
      |						"type": "offers"
      |					}, {
      |						"id": "0000121B0009",
      |						"type": "offers"
      |					}]
      |				},
      |				"priceZones": {
      |					"_embedded": [{
      |						"id": "3",
      |						"type": "price-zones"
      |					}, {
      |						"id": "4",
      |						"type": "price-zones"
      |					}]
      |				}
      |			}
      |		}, {
      |			"id": "13",
      |			"type": "areas",
      |			"attributes": {
      |				"rank": 3,
      |				"name": "FLOOR",
      |				"areaType": "AREA"
      |			},
      |			"relationships": {
      |				"areas": {},
      |				"offers": {
      |					"_embedded": [{
      |						"id": "000000000001",
      |						"type": "offers"
      |					}, {
      |						"id": "00000E160006",
      |						"type": "offers"
      |					}, {
      |						"id": "0000101A0009",
      |						"type": "offers"
      |					}, {
      |						"id": "0000121B0009",
      |						"type": "offers"
      |					}]
      |				},
      |				"priceZones": {
      |					"_embedded": [{
      |						"id": "3",
      |						"type": "price-zones"
      |					}]
      |				}
      |			}
      |		}, {
      |			"id": "11",
      |			"type": "areas",
      |			"attributes": {
      |				"rank": 4,
      |				"name": "LIMITED",
      |				"areaType": "AREA"
      |			},
      |			"relationships": {
      |				"areas": {
      |					"_embedded": [{
      |						"id": "0",
      |						"type": "areas"
      |					}, {
      |						"id": "1",
      |						"type": "areas"
      |					}, {
      |						"id": "2",
      |						"type": "areas"
      |					}]
      |				},
      |				"offers": {
      |					"_embedded": [{
      |						"id": "000000000001",
      |						"type": "offers"
      |					}, {
      |						"id": "00000E160006",
      |						"type": "offers"
      |					}, {
      |						"id": "0000101A0009",
      |						"type": "offers"
      |					}, {
      |						"id": "0000121B0009",
      |						"type": "offers"
      |					}]
      |				},
      |				"priceZones": {
      |					"_embedded": [{
      |						"id": "3",
      |						"type": "price-zones"
      |					}, {
      |						"id": "4",
      |						"type": "price-zones"
      |					}, {
      |						"id": "5",
      |						"type": "price-zones"
      |					}, {
      |						"id": "7",
      |						"type": "price-zones"
      |					}]
      |				}
      |			}
      |		}]
      |	},
      |	"passwords": {
      |		"_embedded": [{
      |			"id": "9f73d35d0775ade1d899a6f91c09f145",
      |			"type": "passwords",
      |			"attributes": {
      |				"name": "Citi Offer",
      |				"type": "password_server",
      |				"text": "Password Information: Enter the first 6 digits of your Citi credit card or Citibank Debit MasterCard account number (no dashes).  Offer open to Citi® MasterCard® cardholders who are legal residents of the 50 United States or District of Columbia. To purchase offer tickets you must make your purchase using a valid Citi® MasterCard®. Limited time offer, available on a first come, first serve basis while supplies last. Tickets are final and non-refundable. Tickets may be resold subject to local governing rules and regulations. Tickets do not include transportation to or from the event or other incidental expenses. Substitutions, cash redemption, returns and exchanges are not permitted. Offer may be modified or withdrawn without prior notice. MasterCard International Incorporated and Citi shall not be liable for any loss including for any loss by reason of cancellation or postponement of event for any reason. Void where prohibited. By participating, individuals agree to be bound by these Terms and Conditions.",
      |				"link": "http://www.livenation.com/citi/",
      |				"textLabel": "Citi Offer",
      |				"linkLabel": "For more information and participating shows",
      |				"exclusive": false,
      |				"prompts": [{
      |					"text": "Special Offer Code"
      |				}]
      |			},
      |			"relationships": {
      |				"offers": {
      |					"_embedded": [{
      |						"id": "00000E160006",
      |						"type": "offers"
      |					}]
      |				}
      |			},
      |			"metadata": {
      |				"type": "password-meta"
      |			}
      |		}]
      |	},
      |	"_embedded": [{
      |		"id": "000000000001",
      |		"type": "offers",
      |		"attributes": {
      |			"name": "ADULT",
      |			"description": "Standard Ticket",
      |			"rank": 0,
      |			"offerType": "DEFAULT",
      |			"currency": "USD",
      |			"prices": [{
      |				"priceZone": "3",
      |				"value": "174.50",
      |				"total": "207.90",
      |				"fees": [{
      |					"value": "0.00",
      |					"label": "Distance",
      |					"type": "distance"
      |				}, {
      |					"value": "5.00",
      |					"label": "Facility",
      |					"type": "facility"
      |				}, {
      |					"value": "28.40",
      |					"label": "Service",
      |					"type": "service"
      |				}],
      |				"taxes": [{
      |					"value": "0.00",
      |					"label": "Face Value Tax",
      |					"type": "face_value_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax",
      |					"type": "service_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax 2",
      |					"type": "service_tax2"
      |				}]
      |			}, {
      |				"priceZone": "4",
      |				"value": "124.50",
      |				"total": "152.70",
      |				"fees": [{
      |					"value": "0.00",
      |					"label": "Distance",
      |					"type": "distance"
      |				}, {
      |					"value": "5.00",
      |					"label": "Facility",
      |					"type": "facility"
      |				}, {
      |					"value": "23.20",
      |					"label": "Service",
      |					"type": "service"
      |				}],
      |				"taxes": [{
      |					"value": "0.00",
      |					"label": "Face Value Tax",
      |					"type": "face_value_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax",
      |					"type": "service_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax 2",
      |					"type": "service_tax2"
      |				}]
      |			}, {
      |				"priceZone": "5",
      |				"value": "74.50",
      |				"total": "98.30",
      |				"fees": [{
      |					"value": "0.00",
      |					"label": "Distance",
      |					"type": "distance"
      |				}, {
      |					"value": "5.00",
      |					"label": "Facility",
      |					"type": "facility"
      |				}, {
      |					"value": "18.80",
      |					"label": "Service",
      |					"type": "service"
      |				}],
      |				"taxes": [{
      |					"value": "0.00",
      |					"label": "Face Value Tax",
      |					"type": "face_value_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax",
      |					"type": "service_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax 2",
      |					"type": "service_tax2"
      |				}]
      |			}, {
      |				"priceZone": "7",
      |				"value": "44.50",
      |				"total": "60.65",
      |				"fees": [{
      |					"value": "0.00",
      |					"label": "Distance",
      |					"type": "distance"
      |				}, {
      |					"value": "5.00",
      |					"label": "Facility",
      |					"type": "facility"
      |				}, {
      |					"value": "11.15",
      |					"label": "Service",
      |					"type": "service"
      |				}],
      |				"taxes": [{
      |					"value": "0.00",
      |					"label": "Face Value Tax",
      |					"type": "face_value_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax",
      |					"type": "service_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax 2",
      |					"type": "service_tax2"
      |				}]
      |			}, {
      |				"priceZone": "8",
      |				"value": "24.50",
      |				"total": "38.75",
      |				"fees": [{
      |					"value": "0.00",
      |					"label": "Distance",
      |					"type": "distance"
      |				}, {
      |					"value": "5.00",
      |					"label": "Facility",
      |					"type": "facility"
      |				}, {
      |					"value": "9.25",
      |					"label": "Service",
      |					"type": "service"
      |				}],
      |				"taxes": [{
      |					"value": "0.00",
      |					"label": "Face Value Tax",
      |					"type": "face_value_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax",
      |					"type": "service_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax 2",
      |					"type": "service_tax2"
      |				}]
      |			}],
      |			"limit": {
      |				"min": 1,
      |				"max": 8,
      |				"multiples": 1
      |			}
      |		},
      |		"relationships": {
      |			"areas": {
      |				"_embedded": [{
      |					"id": "0",
      |					"type": "areas"
      |				}, {
      |					"id": "1",
      |					"type": "areas"
      |				}, {
      |					"id": "2",
      |					"type": "areas"
      |				}, {
      |					"id": "11",
      |					"type": "areas"
      |				}, {
      |					"id": "13",
      |					"type": "areas"
      |				}]
      |			},
      |			"priceZones": {
      |				"_embedded": [{
      |					"id": "3",
      |					"type": "price-zones"
      |				}, {
      |					"id": "4",
      |					"type": "price-zones"
      |				}, {
      |					"id": "5",
      |					"type": "price-zones"
      |				}, {
      |					"id": "7",
      |					"type": "price-zones"
      |				}, {
      |					"id": "8",
      |					"type": "price-zones"
      |				}]
      |			},
      |			"products": {
      |				"_embedded": [{
      |					"id": "0000503AC36E4726",
      |					"type": "products"
      |				}]
      |			}
      |		}
      |	}, {
      |		"id": "0000101A0009",
      |		"type": "offers",
      |		"attributes": {
      |			"name": "VIP1",
      |			"description": "TOP 10 PARTY PACKAGE",
      |			"rank": 1,
      |			"offerDetails": {
      |				"link": "http://www.ticketmaster.com/promo/zfz1l1?ac_link=ntm_coldplay16_tkt_type",
      |				"linkText": "See Full Details!",
      |				"text": "TOP 10 PARTY PACKAGE includes a Premier seating in the first 10 rows- Access to private pre-show lounge with food- drinks- music- Exclusive VIP tour gift item- Official Coldplay VIP laminate- Numbered limited edition Coldplay tour poster for VIPs only- VIP commemorative concert ticket- Exclusive Coldplay guitar pick set- Early entrance into venue- Crowd-less tour merchandise shopping- and a VIP Concierge team on site."
      |			},
      |			"offerType": "SPECIAL",
      |			"currency": "USD",
      |			"prices": [{
      |				"priceZone": "3",
      |				"value": "720.00",
      |				"total": "725.00",
      |				"fees": [{
      |					"value": "0.00",
      |					"label": "Distance",
      |					"type": "distance"
      |				}, {
      |					"value": "5.00",
      |					"label": "Facility",
      |					"type": "facility"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service",
      |					"type": "service"
      |				}],
      |				"taxes": [{
      |					"value": "0.00",
      |					"label": "Face Value Tax",
      |					"type": "face_value_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax",
      |					"type": "service_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax 2",
      |					"type": "service_tax2"
      |				}]
      |			}, {
      |				"priceZone": "4",
      |				"value": "720.00",
      |				"total": "725.00",
      |				"fees": [{
      |					"value": "0.00",
      |					"label": "Distance",
      |					"type": "distance"
      |				}, {
      |					"value": "5.00",
      |					"label": "Facility",
      |					"type": "facility"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service",
      |					"type": "service"
      |				}],
      |				"taxes": [{
      |					"value": "0.00",
      |					"label": "Face Value Tax",
      |					"type": "face_value_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax",
      |					"type": "service_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax 2",
      |					"type": "service_tax2"
      |				}]
      |			}, {
      |				"priceZone": "5",
      |				"value": "720.00",
      |				"total": "725.00",
      |				"fees": [{
      |					"value": "0.00",
      |					"label": "Distance",
      |					"type": "distance"
      |				}, {
      |					"value": "5.00",
      |					"label": "Facility",
      |					"type": "facility"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service",
      |					"type": "service"
      |				}],
      |				"taxes": [{
      |					"value": "0.00",
      |					"label": "Face Value Tax",
      |					"type": "face_value_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax",
      |					"type": "service_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax 2",
      |					"type": "service_tax2"
      |				}]
      |			}, {
      |				"priceZone": "7",
      |				"value": "720.00",
      |				"total": "725.00",
      |				"fees": [{
      |					"value": "0.00",
      |					"label": "Distance",
      |					"type": "distance"
      |				}, {
      |					"value": "5.00",
      |					"label": "Facility",
      |					"type": "facility"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service",
      |					"type": "service"
      |				}],
      |				"taxes": [{
      |					"value": "0.00",
      |					"label": "Face Value Tax",
      |					"type": "face_value_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax",
      |					"type": "service_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax 2",
      |					"type": "service_tax2"
      |				}]
      |			}, {
      |				"priceZone": "8",
      |				"value": "720.00",
      |				"total": "725.00",
      |				"fees": [{
      |					"value": "0.00",
      |					"label": "Distance",
      |					"type": "distance"
      |				}, {
      |					"value": "5.00",
      |					"label": "Facility",
      |					"type": "facility"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service",
      |					"type": "service"
      |				}],
      |				"taxes": [{
      |					"value": "0.00",
      |					"label": "Face Value Tax",
      |					"type": "face_value_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax",
      |					"type": "service_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax 2",
      |					"type": "service_tax2"
      |				}]
      |			}],
      |			"limit": {
      |				"min": 1,
      |				"max": 4,
      |				"multiples": 1
      |			}
      |		},
      |		"relationships": {
      |			"areas": {
      |				"_embedded": [{
      |					"id": "0",
      |					"type": "areas"
      |				}, {
      |					"id": "1",
      |					"type": "areas"
      |				}, {
      |					"id": "2",
      |					"type": "areas"
      |				}, {
      |					"id": "11",
      |					"type": "areas"
      |				}, {
      |					"id": "13",
      |					"type": "areas"
      |				}]
      |			},
      |			"priceZones": {
      |				"_embedded": [{
      |					"id": "3",
      |					"type": "price-zones"
      |				}, {
      |					"id": "4",
      |					"type": "price-zones"
      |				}, {
      |					"id": "5",
      |					"type": "price-zones"
      |				}, {
      |					"id": "7",
      |					"type": "price-zones"
      |				}, {
      |					"id": "8",
      |					"type": "price-zones"
      |				}]
      |			},
      |			"products": {
      |				"_embedded": [{
      |					"id": "0000503AC36E4726",
      |					"type": "products"
      |				}]
      |			}
      |		}
      |	}, {
      |		"id": "0000121B0009",
      |		"type": "offers",
      |		"attributes": {
      |			"name": "VIP2",
      |			"description": "TOP 25 TOUR PACKAGE",
      |			"rank": 2,
      |			"offerDetails": {
      |				"link": "http://www.ticketmaster.com/promo/zfz1l1?ac_link=ntm_coldplay16_tkt_type",
      |				"linkText": "See Full Details!",
      |				"text": "TOP 25 TOUR PACKAGE includes a Premier seating in the first 25 rows- Exclusive VIP tour gift item- Numbered limited edition Coldplay tour poster for VIPs only- VIP commemorative concert ticket- and an Exclusive Coldplay guitar pick and a VIP Concierge team on site."
      |			},
      |			"offerType": "SPECIAL",
      |			"currency": "USD",
      |			"prices": [{
      |				"priceZone": "3",
      |				"value": "490.00",
      |				"total": "495.00",
      |				"fees": [{
      |					"value": "0.00",
      |					"label": "Distance",
      |					"type": "distance"
      |				}, {
      |					"value": "5.00",
      |					"label": "Facility",
      |					"type": "facility"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service",
      |					"type": "service"
      |				}],
      |				"taxes": [{
      |					"value": "0.00",
      |					"label": "Face Value Tax",
      |					"type": "face_value_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax",
      |					"type": "service_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax 2",
      |					"type": "service_tax2"
      |				}]
      |			}, {
      |				"priceZone": "4",
      |				"value": "490.00",
      |				"total": "495.00",
      |				"fees": [{
      |					"value": "0.00",
      |					"label": "Distance",
      |					"type": "distance"
      |				}, {
      |					"value": "5.00",
      |					"label": "Facility",
      |					"type": "facility"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service",
      |					"type": "service"
      |				}],
      |				"taxes": [{
      |					"value": "0.00",
      |					"label": "Face Value Tax",
      |					"type": "face_value_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax",
      |					"type": "service_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax 2",
      |					"type": "service_tax2"
      |				}]
      |			}, {
      |				"priceZone": "5",
      |				"value": "490.00",
      |				"total": "495.00",
      |				"fees": [{
      |					"value": "0.00",
      |					"label": "Distance",
      |					"type": "distance"
      |				}, {
      |					"value": "5.00",
      |					"label": "Facility",
      |					"type": "facility"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service",
      |					"type": "service"
      |				}],
      |				"taxes": [{
      |					"value": "0.00",
      |					"label": "Face Value Tax",
      |					"type": "face_value_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax",
      |					"type": "service_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax 2",
      |					"type": "service_tax2"
      |				}]
      |			}, {
      |				"priceZone": "7",
      |				"value": "490.00",
      |				"total": "495.00",
      |				"fees": [{
      |					"value": "0.00",
      |					"label": "Distance",
      |					"type": "distance"
      |				}, {
      |					"value": "5.00",
      |					"label": "Facility",
      |					"type": "facility"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service",
      |					"type": "service"
      |				}],
      |				"taxes": [{
      |					"value": "0.00",
      |					"label": "Face Value Tax",
      |					"type": "face_value_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax",
      |					"type": "service_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax 2",
      |					"type": "service_tax2"
      |				}]
      |			}, {
      |				"priceZone": "8",
      |				"value": "490.00",
      |				"total": "495.00",
      |				"fees": [{
      |					"value": "0.00",
      |					"label": "Distance",
      |					"type": "distance"
      |				}, {
      |					"value": "5.00",
      |					"label": "Facility",
      |					"type": "facility"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service",
      |					"type": "service"
      |				}],
      |				"taxes": [{
      |					"value": "0.00",
      |					"label": "Face Value Tax",
      |					"type": "face_value_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax",
      |					"type": "service_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax 2",
      |					"type": "service_tax2"
      |				}]
      |			}],
      |			"limit": {
      |				"min": 1,
      |				"max": 4,
      |				"multiples": 1
      |			}
      |		},
      |		"relationships": {
      |			"areas": {
      |				"_embedded": [{
      |					"id": "0",
      |					"type": "areas"
      |				}, {
      |					"id": "1",
      |					"type": "areas"
      |				}, {
      |					"id": "2",
      |					"type": "areas"
      |				}, {
      |					"id": "11",
      |					"type": "areas"
      |				}, {
      |					"id": "13",
      |					"type": "areas"
      |				}]
      |			},
      |			"priceZones": {
      |				"_embedded": [{
      |					"id": "3",
      |					"type": "price-zones"
      |				}, {
      |					"id": "4",
      |					"type": "price-zones"
      |				}, {
      |					"id": "5",
      |					"type": "price-zones"
      |				}, {
      |					"id": "7",
      |					"type": "price-zones"
      |				}, {
      |					"id": "8",
      |					"type": "price-zones"
      |				}]
      |			},
      |			"products": {
      |				"_embedded": [{
      |					"id": "0000503AC36E4726",
      |					"type": "products"
      |				}]
      |			}
      |		}
      |	}, {
      |		"id": "00000E160006",
      |		"type": "offers",
      |		"attributes": {
      |			"name": "CITPO",
      |			"description": "Citi® Cardmember Preferred Tickets",
      |			"rank": 3,
      |			"offerType": "SPECIAL",
      |			"currency": "USD",
      |			"prices": [{
      |				"priceZone": "3",
      |				"value": "174.50",
      |				"total": "207.90",
      |				"fees": [{
      |					"value": "0.00",
      |					"label": "Distance",
      |					"type": "distance"
      |				}, {
      |					"value": "5.00",
      |					"label": "Facility",
      |					"type": "facility"
      |				}, {
      |					"value": "28.40",
      |					"label": "Service",
      |					"type": "service"
      |				}],
      |				"taxes": [{
      |					"value": "0.00",
      |					"label": "Face Value Tax",
      |					"type": "face_value_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax",
      |					"type": "service_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax 2",
      |					"type": "service_tax2"
      |				}]
      |			}, {
      |				"priceZone": "4",
      |				"value": "124.50",
      |				"total": "152.70",
      |				"fees": [{
      |					"value": "0.00",
      |					"label": "Distance",
      |					"type": "distance"
      |				}, {
      |					"value": "5.00",
      |					"label": "Facility",
      |					"type": "facility"
      |				}, {
      |					"value": "23.20",
      |					"label": "Service",
      |					"type": "service"
      |				}],
      |				"taxes": [{
      |					"value": "0.00",
      |					"label": "Face Value Tax",
      |					"type": "face_value_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax",
      |					"type": "service_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax 2",
      |					"type": "service_tax2"
      |				}]
      |			}, {
      |				"priceZone": "5",
      |				"value": "74.50",
      |				"total": "98.30",
      |				"fees": [{
      |					"value": "0.00",
      |					"label": "Distance",
      |					"type": "distance"
      |				}, {
      |					"value": "5.00",
      |					"label": "Facility",
      |					"type": "facility"
      |				}, {
      |					"value": "18.80",
      |					"label": "Service",
      |					"type": "service"
      |				}],
      |				"taxes": [{
      |					"value": "0.00",
      |					"label": "Face Value Tax",
      |					"type": "face_value_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax",
      |					"type": "service_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax 2",
      |					"type": "service_tax2"
      |				}]
      |			}, {
      |				"priceZone": "7",
      |				"value": "44.50",
      |				"total": "60.65",
      |				"fees": [{
      |					"value": "0.00",
      |					"label": "Distance",
      |					"type": "distance"
      |				}, {
      |					"value": "5.00",
      |					"label": "Facility",
      |					"type": "facility"
      |				}, {
      |					"value": "11.15",
      |					"label": "Service",
      |					"type": "service"
      |				}],
      |				"taxes": [{
      |					"value": "0.00",
      |					"label": "Face Value Tax",
      |					"type": "face_value_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax",
      |					"type": "service_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax 2",
      |					"type": "service_tax2"
      |				}]
      |			}, {
      |				"priceZone": "8",
      |				"value": "24.50",
      |				"total": "38.75",
      |				"fees": [{
      |					"value": "0.00",
      |					"label": "Distance",
      |					"type": "distance"
      |				}, {
      |					"value": "5.00",
      |					"label": "Facility",
      |					"type": "facility"
      |				}, {
      |					"value": "9.25",
      |					"label": "Service",
      |					"type": "service"
      |				}],
      |				"taxes": [{
      |					"value": "0.00",
      |					"label": "Face Value Tax",
      |					"type": "face_value_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax",
      |					"type": "service_tax"
      |				}, {
      |					"value": "0.00",
      |					"label": "Service Tax 2",
      |					"type": "service_tax2"
      |				}]
      |			}],
      |			"limit": {
      |				"min": 1,
      |				"max": 8,
      |				"multiples": 1
      |			}
      |		},
      |		"relationships": {
      |			"areas": {
      |				"_embedded": [{
      |					"id": "0",
      |					"type": "areas"
      |				}, {
      |					"id": "1",
      |					"type": "areas"
      |				}, {
      |					"id": "2",
      |					"type": "areas"
      |				}, {
      |					"id": "11",
      |					"type": "areas"
      |				}, {
      |					"id": "13",
      |					"type": "areas"
      |				}]
      |			},
      |			"priceZones": {
      |				"_embedded": [{
      |					"id": "3",
      |					"type": "price-zones"
      |				}, {
      |					"id": "4",
      |					"type": "price-zones"
      |				}, {
      |					"id": "5",
      |					"type": "price-zones"
      |				}, {
      |					"id": "7",
      |					"type": "price-zones"
      |				}, {
      |					"id": "8",
      |					"type": "price-zones"
      |				}]
      |			},
      |			"passwords": {
      |				"_embedded": [{
      |					"id": "9f73d35d0775ade1d899a6f91c09f145",
      |					"type": "passwords"
      |				}]
      |			},
      |			"products": {
      |				"_embedded": [{
      |					"id": "0000503AC36E4726",
      |					"type": "products"
      |				}]
      |			}
      |		}
      |	}]
      |}
    """.stripMargin
}