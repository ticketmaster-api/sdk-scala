package com.ticketmaster.api.discovery

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import com.ticketmaster.api.discovery.Filter.NoFilter
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

sealed trait Filter[+T] {
  def isDefined: Boolean

  def value: T
}

object Filter {
  final case object NoFilter extends Filter[Nothing] {
    override def isDefined: Boolean = false

    override def value: Nothing = throw new NoSuchElementException()
  }

  final case class Filtered[T](value: T) extends Filter[T] {
    override def isDefined: Boolean = true
  }

  implicit def stringToValue(s: String) = Filtered(s)

  implicit def intToValue(i: Int) = Filtered(i)

  implicit def seqToValue[T](s: Seq[T]) = Filtered(s)

  implicit def dateTimeToValue[T](t: ZonedDateTime) = Filtered(t)

  implicit def stringifyQueryParams(queryParamMap: Map[String, Filter[_]]): Map[String, String] = {
    val stringify: PartialFunction[Any, String] = {
      case z: ZonedDateTime => z.format(DateTimeFormatter.ISO_INSTANT)
      case s: Seq[_] => s.mkString(",")
      case a => a.toString
    }

    queryParamMap.filter { f: (String, Filter[_]) => f._2.isDefined }
      .map(d => (d._1, stringify(d._2.value)))
  }
}

//todo may want to generalise requests - by id and by page
trait PageRequest {
  def size: Filter[Int]

  def page: Filter[Int]

  def sort: Filter[String]
}

case class RateLimits(rateLimit: Int,
                      available: Int,
                      over: Int,
                      reset: ZonedDateTime)

case class Page(size: Int,
                totalElements: Int,
                totalPages: Int,
                number: Int)

case class Link(href: String, templated: Option[Boolean])

case class Links(self: Link)

//todo this could be a stream, stream iterator
case class PageResult[T](_embedded: T, page: Page, _links: Links)

trait Response[T] {
  def result: T

  def rateLimits: RateLimits
}

trait PageResponse[T] {
  def pageResult: PageResult[T]

  def rateLimits: RateLimits
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

case class ApiException(message: String) extends Exception(message)

class ResourceNotFoundException(message: String) extends ApiException(message)