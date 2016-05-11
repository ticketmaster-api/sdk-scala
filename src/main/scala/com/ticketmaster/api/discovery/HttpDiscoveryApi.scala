package com.ticketmaster.api.discovery

import java.time.{Instant, ZoneId, ZonedDateTime}

import argonaut.Argonaut._
import argonaut._
import com.ticketmaster.api.discovery.Filter._
import com.ticketmaster.api.discovery.domain.{Segment => CSegment, _}
import com.ticketmaster.api.discovery.http.{DispatchHttp, Http}
import com.ticketmaster.api.discovery.http.protocol.{HttpRequest, HttpResponse}
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{Failure, Success}

trait HttpDiscoveryApi extends DiscoveryApi {

  val LOGGER = LoggerFactory.getLogger(getClass)

  val ROOT_URL = "https://app.ticketmaster.com/discovery/v2"

  val USER_AGENT = "Ticketmaster Discovery Scala"

  val okRange = 200 to 299

  val apiKey: String

  def http: Http = new DispatchHttp

  override def searchEvents(searchEventsRequest: SearchEventsRequest)(implicit ec: ExecutionContext): Future[PageResponse[Events]] = {
    val filters = Map[String, Filter[_]]()
      .updated("keyword", searchEventsRequest.keyword)
      .updated("attractionId", searchEventsRequest.attractionId)
      .updated("deviceId", searchEventsRequest.deviceId)
      .updated("locale", searchEventsRequest.locale)
      .updated("marketId", searchEventsRequest.marketId)
      .updated("page", searchEventsRequest.page)
      .updated("promoterId", searchEventsRequest.promoterId)
      .updated("size", searchEventsRequest.size)
      .updated("sort", searchEventsRequest.sort)
      .updated("source", searchEventsRequest.source)
      .updated("venueId", searchEventsRequest.venueId)
      .updated("latlong", searchEventsRequest.latlong)
      .updated("postalCode", searchEventsRequest.postalCode)
      .updated("radius", searchEventsRequest.radius)
      .updated("startDateTime", searchEventsRequest.startDateTime)
      .updated("endDateTime", searchEventsRequest.endDateTime)
      .updated("includeTest", searchEventsRequest.includeTest)
      .updated("includeTBD", searchEventsRequest.includeTbd)
      .updated("includeTBA", searchEventsRequest.includeTba)

    val req = HttpRequest(root = ROOT_URL, queryParams = filters) / "events.json"

    handle(req, response => SearchEventsResponse(decode[PageResult[Events]](response.body.get), extractRateLimitInfo(response)))
  }

  override def getEvent(getEventRequest: GetEventRequest)(implicit ec: ExecutionContext): Future[Response[Event]] = {
    val filters = Map[String, Filter[_]]()
      .updated("locale", getEventRequest.locale)

    val req = HttpRequest(root = ROOT_URL, queryParams = filters) / "events" / (getEventRequest.id + ".json")

    handle(req, response => GetEventResponse(decode[Event](response.body.get), extractRateLimitInfo(response)))
  }

  override def getEventImages(getEventImagesRequest: GetEventImagesRequest)(implicit ec: ExecutionContext): dispatch.Future[Response[EventImages]] = {
    val filters = Map[String, Filter[_]]()
      .updated("locale", getEventImagesRequest.locale)

    val req = HttpRequest(root = ROOT_URL, queryParams = filters) / "events" / getEventImagesRequest.id / "images.json"

    handle(req, response => GetEventImagesResponse(decode[EventImages](response.body.get), extractRateLimitInfo(response)))
  }

  override def searchAttractions(searchAttractionsRequest: SearchAttractionsRequest)(implicit ec: ExecutionContext): dispatch.Future[PageResponse[Attractions]] = {
    val filters = Map[String, Filter[_]]()
      .updated("keyword", searchAttractionsRequest.keyword)
      .updated("locale", searchAttractionsRequest.locale)
      .updated("page", searchAttractionsRequest.page)
      .updated("size", searchAttractionsRequest.size)
      .updated("sort", searchAttractionsRequest.sort)

    val req = HttpRequest(root = ROOT_URL, queryParams = filters) / "attractions.json"

    handle(req, response => SearchAttractionsResponse(decode[PageResult[Attractions]](response.body.get), extractRateLimitInfo(response)))
  }

  override def getAttraction(getAttractionRequest: GetAttractionRequest)(implicit ec: ExecutionContext): Future[GetAttractionResponse] = {
    val filters = Map[String, Filter[_]]()
      .updated("locale", getAttractionRequest.locale)

    val req = HttpRequest(root = ROOT_URL, queryParams = filters) / "attractions" / (getAttractionRequest.id + ".json")

    handle(req, response => GetAttractionResponse(decode[Attraction](response.body.get), extractRateLimitInfo(response)))
  }

  override def searchVenues(searchVenuesRequest: SearchVenuesRequest)(implicit ec: ExecutionContext): dispatch.Future[PageResponse[Venues]] = {
    val filters = Map[String, Filter[_]]()
      .updated("keyword", searchVenuesRequest.keyword)
      .updated("locale", searchVenuesRequest.locale)
      .updated("page", searchVenuesRequest.page)
      .updated("size", searchVenuesRequest.size)
      .updated("sort", searchVenuesRequest.sort)

    val req = HttpRequest(root = ROOT_URL, queryParams = filters) / "venues.json"

    handle(req, response => SearchVenuesResponse(decode[PageResult[Venues]](response.body.get), extractRateLimitInfo(response)))
  }

  override def getVenue(getVenueRequest: GetVenueRequest)(implicit ec: ExecutionContext): dispatch.Future[Response[Venue]] = {
    val filters = Map[String, Filter[_]]()
      .updated("locale", getVenueRequest.locale)

    val req = HttpRequest(root = ROOT_URL, queryParams = filters) / "venues" / (getVenueRequest.id + ".json")

    handle(req, response => GetVenueResponse(decode[Venue](response.body.get), extractRateLimitInfo(response)))
  }

  override def searchClassifications(searchClassificationsRequest: SearchClassificationsRequest)(implicit ec: ExecutionContext): dispatch.Future[PageResponse[Classifications]] = {
    val filters = Map[String, Filter[_]]()
      .updated("keyword", searchClassificationsRequest.keyword)
      .updated("page", searchClassificationsRequest.page)
      .updated("size", searchClassificationsRequest.size)
      .updated("sort", searchClassificationsRequest.sort)

    val req = HttpRequest(root = ROOT_URL, queryParams = filters) / "classifications.json"

    handle(req, response => SearchClassificationsResponse(decode[PageResult[Classifications]](response.body.get), extractRateLimitInfo(response)))
  }

  override def getClassification(getClassificationRequest: GetClassificationRequest)(implicit ec: ExecutionContext): dispatch.Future[Response[Classification]] = {
    val filters = Map[String, Filter[_]]()
      .updated("locale", getClassificationRequest.locale)

    val req = HttpRequest(root = ROOT_URL, queryParams = filters) / "classifications" / (getClassificationRequest.id + ".json")

    handle(req, response => GetClassificationResponse(decode[Classification](response.body.get), extractRateLimitInfo(response)))
  }

  private def handle[T](req: HttpRequest, handler: HttpResponse => T)(implicit ec: ExecutionContext) = {

    LOGGER.debug(s"Request: ${req}")

    http(req
      .addQueryParameter("apikey", apiKey)
      .addHeader("User-Agent", s"${USER_AGENT}/${build.Info.version}")
    ).map(res => respond(res, handler))
  }

  // based on status code either execute handler or handle error
  private def respond[T](response: HttpResponse, handler: HttpResponse => T) = {
    def extractMessage = {
      def extractDetail(json: Json): Option[Cursor] = {
        val maybeDetail = for {
          errors <- json.cursor --\ "errors"
          first <- errors.downArray
          detail <- first --\ ("detail")
        } yield detail
        maybeDetail
      }
      val body = response.body.get
      Parse.parse(body).fold(
        err => s"Failed to parse response: ${err}",
        json => extractDetail(json).fold(s"Failed to find error details: ${json}")(_.focus.stringOr(body))
      )
    }

    response.status match {
      case c if okRange contains c => handler(response)
      case 404 => throw new ResourceNotFoundException(extractMessage)
      case _ => throw ApiException(extractMessage)
    }
  }

  private def extractRateLimitInfo(response: HttpResponse) = {
    RateLimits(
      response.headers("Rate-Limit").toInt,
      response.headers("Rate-Limit-Available").toInt,
      response.headers("Rate-Limit-Over").toInt,
      ZonedDateTime.ofInstant(Instant.ofEpochMilli(response.headers("Rate-Limit-Reset").toLong), ZoneId.of("UTC")))
  }

  //todo maybe extract this and make generic so can switch out argonaut - split this out into another class
  private def decode[T](json: String)(implicit decode: DecodeJson[T]): T = {
    Parse.decodeValidation[T](json) match {
      case Success(s) => s
      case Failure(e) => throw ApiException(s"$e in $json")
    }
  }

  implicit def SearchEventsResultCodec: CodecJson[PageResult[Events]] = casecodec3(PageResult.apply[Events], PageResult.unapply[Events])("_embedded", "page", "_links")

  implicit def SearchAttractionsResultCodec: CodecJson[PageResult[Attractions]] = casecodec3(PageResult.apply[Attractions], PageResult.unapply[Attractions])("_embedded", "page", "_links")

  implicit def SearchVenuesResultCodec: CodecJson[PageResult[Venues]] = casecodec3(PageResult.apply[Venues], PageResult.unapply[Venues])("_embedded", "page", "_links")

  implicit def SearchClassificationsResultCodec: CodecJson[PageResult[Classifications]] = casecodec3(PageResult.apply[Classifications], PageResult.unapply[Classifications])("_embedded", "page", "_links")

  implicit def EventsCodec: CodecJson[Events] = casecodec1(Events.apply, Events.unapply)("events")

  implicit def EventCodec: CodecJson[Event] = casecodec11(Event.apply, Event.unapply)("id", "name", "locale", "url", "promoter", "sales", "dates", "test", "type", "images", "classifications")

  implicit def EventImagesResultCodec: CodecJson[EventImages] = casecodec3(EventImages.apply, EventImages.unapply)("type", "id", "images")

  implicit def AttractionsCodec: CodecJson[Attractions] = casecodec1(Attractions.apply, Attractions.unapply)("attractions")

  implicit def AttractionCodec: CodecJson[Attraction] = casecodec2(Attraction.apply, Attraction.unapply)("id", "name")

  implicit def VenuesCodec: CodecJson[Venues] = casecodec1(Venues.apply, Venues.unapply)("venues")

  implicit def VenueCodec: CodecJson[Venue] = casecodec2(Venue.apply, Venue.unapply)("id", "name")

  implicit def ClassificationsCodec: CodecJson[Classifications] = casecodec1(Classifications.apply, Classifications.unapply)("classifications")

  implicit def ClassificationCodec: CodecJson[Classification] = casecodec1(Classification.apply, Classification.unapply)("segment")

  implicit def EventClassificationCodec: CodecJson[EventClassification] = casecodec4(EventClassification.apply, EventClassification.unapply)("primary", "segment", "genre", "subgenre")

  implicit def PromoterCodec: CodecJson[Promoter] = casecodec1(Promoter.apply, Promoter.unapply)("id")

  implicit def ImageCodec: CodecJson[Image] = casecodec5(Image.apply, Image.unapply)("ratio", "url", "width", "height", "fallback")

  implicit def SalesCodec: CodecJson[Sales] = casecodec1(Sales.apply, Sales.unapply)("public")

  implicit def PublicSalesCodec: CodecJson[PublicSales] = casecodec3(PublicSales.apply, PublicSales.unapply)("startDateTime", "startTBD", "endDateTime")

  implicit def GenreCodec: CodecJson[Genre] = casecodec2(Genre.apply, Genre.unapply)("id", "name")

  implicit def DatesCodec: CodecJson[Dates] = casecodec3(Dates.apply, Dates.unapply)("start", "timezone", "status")

  implicit def StatusCodec: CodecJson[Status] = casecodec1(Status.apply, Status.unapply)("code")

  implicit def DateCodec: CodecJson[Date] = casecodec7(Date.apply, Date.unapply)("dateTime", "localDate", "localTime", "dateTBD", "dateTBA", "timeTBA", "noSpecificTime")

  implicit def SegmentCodec: CodecJson[CSegment] = casecodec2(CSegment.apply, CSegment.unapply)("id", "name")

  implicit def LinksCodec: CodecJson[Links] = casecodec1(Links.apply, Links.unapply)("self")

  implicit def LinkCodec: CodecJson[Link] = casecodec2(Link.apply, Link.unapply)("href", "templated")

  implicit def PageCodec: CodecJson[Page] = casecodec4(Page.apply, Page.unapply)("size", "totalElements", "totalPages", "number")

}
