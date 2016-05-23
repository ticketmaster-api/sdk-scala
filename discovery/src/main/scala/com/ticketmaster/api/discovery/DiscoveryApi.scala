package com.ticketmaster.api.discovery

import java.time.ZonedDateTime

import com.ticketmaster.api.Api.Filter.NoFilter
import com.ticketmaster.api.Api._
import com.ticketmaster.api.discovery.domain._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

trait DiscoveryApi {
  def searchEvents(searchEventsRequest: SearchEventsRequest = SearchEventsRequest())(implicit ec: ExecutionContext): Future[PageResponse[Events]]

  def getEvent(getEventRequest: GetEventRequest)(implicit ec: ExecutionContext): Future[Response[Event]]

  def getEventImages(getEventImagesRequest: GetEventImagesRequest)(implicit ec: ExecutionContext): Future[Response[EventImages]]

  def searchAttractions(searchAttractionsRequest: SearchAttractionsRequest = SearchAttractionsRequest())(implicit ec: ExecutionContext): Future[PageResponse[Attractions]]

  def getAttraction(getAttractionRequest: GetAttractionRequest)(implicit ec: ExecutionContext): Future[Response[Attraction]]

  def searchVenues(searchVenuesRequest: SearchVenuesRequest = SearchVenuesRequest())(implicit ec: ExecutionContext): Future[PageResponse[Venues]]

  def getVenue(getVenueRequest: GetVenueRequest)(implicit ec: ExecutionContext): Future[Response[Venue]]

  def searchClassifications(searchClassificationsRequest: SearchClassificationsRequest = SearchClassificationsRequest())(implicit ec: ExecutionContext): Future[PageResponse[Classifications]]

  def getClassification(getClassificationRequest: GetClassificationRequest)(implicit ec: ExecutionContext): Future[Response[Classification]]
}

case class SearchEventsRequest(keyword: Filter[String] = NoFilter,
                               attractionId: Filter[Seq[String]] = NoFilter,
                               venueId: Filter[Seq[String]] = NoFilter,
                               promoterId: Filter[Seq[String]] = NoFilter,
                               postalCode: Filter[String] = NoFilter,
                               latlong: Filter[String] = NoFilter,
                               radius: Filter[String] = NoFilter,
                               unit: Filter[String] = NoFilter,
                               source: Filter[String] = NoFilter,
                               locale: Filter[String] = NoFilter,
                               marketId: Filter[String] = NoFilter,
                               deviceId: Filter[String] = NoFilter,
                               startDateTime: Filter[ZonedDateTime] = NoFilter,
                               endDateTime: Filter[ZonedDateTime] = NoFilter,
                               includeTba: Filter[String] = NoFilter,
                               includeTbd: Filter[String] = NoFilter,
                               includeTest: Filter[String] = NoFilter,
                               size: Filter[Int] = NoFilter,
                               page: Filter[Int] = NoFilter,
                               sort: Filter[String] = NoFilter) extends PageRequest

case class GetEventRequest(id: String,
                           locale: Filter[String] = NoFilter)

case class GetEventImagesRequest(id: String,
                                 locale: Filter[String] = NoFilter)

case class SearchAttractionsRequest(keyword: Filter[String] = NoFilter,
                                    locale: Filter[String] = NoFilter,
                                    size: Filter[Int] = NoFilter,
                                    page: Filter[Int] = NoFilter,
                                    sort: Filter[String] = NoFilter) extends PageRequest

case class GetAttractionRequest(id: String,
                                locale: Filter[String] = NoFilter)

case class SearchVenuesRequest(keyword: Filter[String] = NoFilter,
                               locale: Filter[String] = NoFilter,
                               size: Filter[Int] = NoFilter,
                               page: Filter[Int] = NoFilter,
                               sort: Filter[String] = NoFilter) extends PageRequest

case class GetVenueRequest(id: String,
                           locale: Filter[String] = NoFilter)

case class SearchClassificationsRequest(keyword: Filter[String] = NoFilter,
                                        size: Filter[Int] = NoFilter,
                                        page: Filter[Int] = NoFilter,
                                        sort: Filter[String] = NoFilter) extends PageRequest

case class GetClassificationRequest(id: String,
                                    locale: Filter[String] = NoFilter)


case class SearchEventsResponse(pageResult: PageResult[Events],
                                rateLimits: RateLimits) extends PageResponse[Events]

case class GetEventResponse(result: Event,
                            rateLimits: RateLimits) extends Response[Event]

case class GetEventImagesResponse(result: EventImages,
                                  rateLimits: RateLimits) extends Response[EventImages]

case class SearchAttractionsResponse(pageResult: PageResult[Attractions],
                                     rateLimits: RateLimits) extends PageResponse[Attractions]

case class GetAttractionResponse(result: Attraction,
                                 rateLimits: RateLimits) extends Response[Attraction]

case class SearchVenuesResponse(pageResult: PageResult[Venues],
                                rateLimits: RateLimits) extends PageResponse[Venues]

case class GetVenueResponse(result: Venue,
                            rateLimits: RateLimits) extends Response[Venue]

case class SearchClassificationsResponse(pageResult: PageResult[Classifications],
                                         rateLimits: RateLimits) extends PageResponse[Classifications]

case class GetClassificationResponse(result: Classification,
                                     rateLimits: RateLimits) extends Response[Classification]
