package com.ticketmaster.api.discovery

import argonaut.Shapeless._
import com.ticketmaster.api.Api._
import com.ticketmaster.api.discovery.domain._
import com.ticketmaster.api.http.HttpHandler
import com.ticketmaster.api.http.protocol.HttpRequest

import scala.concurrent.{ExecutionContext, Future}


trait HttpDiscoveryApi extends DiscoveryApi with HttpHandler {
  val ROOT_URL = "https://app.ticketmaster.com/discovery/v2"

  val userAgent = "Ticketmaster Discovery Scala"

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

    handleRequest[PageResult[Events], SearchEventsResponse](req, (body, rateLimits) => SearchEventsResponse(body, rateLimits))
  }

  override def getEvent(getEventRequest: GetEventRequest)(implicit ec: ExecutionContext): Future[Response[Event]] = {
    val filters = Map[String, Filter[_]]()
      .updated("locale", getEventRequest.locale)

    val req = HttpRequest(root = ROOT_URL, queryParams = filters) / "events" / (getEventRequest.id + ".json")

    handleRequest[Event, GetEventResponse](req, (body, rateLimits) => GetEventResponse(body, rateLimits))
  }

  override def getEventImages(getEventImagesRequest: GetEventImagesRequest)(implicit ec: ExecutionContext): dispatch.Future[Response[EventImages]] = {
    val filters = Map[String, Filter[_]]()
      .updated("locale", getEventImagesRequest.locale)

    val req = HttpRequest(root = ROOT_URL, queryParams = filters) / "events" / getEventImagesRequest.id / "images.json"

    handleRequest[EventImages, GetEventImagesResponse](req, (body, rateLimits) => GetEventImagesResponse(body, rateLimits))
  }

  override def searchAttractions(searchAttractionsRequest: SearchAttractionsRequest)(implicit ec: ExecutionContext): dispatch.Future[PageResponse[Attractions]] = {
    val filters = Map[String, Filter[_]]()
      .updated("keyword", searchAttractionsRequest.keyword)
      .updated("locale", searchAttractionsRequest.locale)
      .updated("page", searchAttractionsRequest.page)
      .updated("size", searchAttractionsRequest.size)
      .updated("sort", searchAttractionsRequest.sort)

    val req = HttpRequest(root = ROOT_URL, queryParams = filters) / "attractions.json"

    handleRequest[PageResult[Attractions], SearchAttractionsResponse](req, (body, rateLimits) => SearchAttractionsResponse(body, rateLimits))
  }

  override def getAttraction(getAttractionRequest: GetAttractionRequest)(implicit ec: ExecutionContext): Future[GetAttractionResponse] = {
    val filters = Map[String, Filter[_]]()
      .updated("locale", getAttractionRequest.locale)

    val req = HttpRequest(root = ROOT_URL, queryParams = filters) / "attractions" / (getAttractionRequest.id + ".json")

    handleRequest[Attraction, GetAttractionResponse](req, (body, rateLimits) => GetAttractionResponse(body, rateLimits))
  }

  override def searchVenues(searchVenuesRequest: SearchVenuesRequest)(implicit ec: ExecutionContext): dispatch.Future[PageResponse[Venues]] = {
    val filters = Map[String, Filter[_]]()
      .updated("keyword", searchVenuesRequest.keyword)
      .updated("locale", searchVenuesRequest.locale)
      .updated("page", searchVenuesRequest.page)
      .updated("size", searchVenuesRequest.size)
      .updated("sort", searchVenuesRequest.sort)

    val req = HttpRequest(root = ROOT_URL, queryParams = filters) / "venues.json"

    handleRequest[PageResult[Venues], SearchVenuesResponse](req, (body, rateLimits) => SearchVenuesResponse(body, rateLimits))
  }

  override def getVenue(getVenueRequest: GetVenueRequest)(implicit ec: ExecutionContext): dispatch.Future[Response[Venue]] = {
    val filters = Map[String, Filter[_]]()
      .updated("locale", getVenueRequest.locale)

    val req = HttpRequest(root = ROOT_URL, queryParams = filters) / "venues" / (getVenueRequest.id + ".json")

    handleRequest[Venue, GetVenueResponse](req, (body, rateLimits) => GetVenueResponse(body, rateLimits))
  }

  override def searchClassifications(searchClassificationsRequest: SearchClassificationsRequest)(implicit ec: ExecutionContext): dispatch.Future[PageResponse[Classifications]] = {
    val filters = Map[String, Filter[_]]()
      .updated("keyword", searchClassificationsRequest.keyword)
      .updated("page", searchClassificationsRequest.page)
      .updated("size", searchClassificationsRequest.size)
      .updated("sort", searchClassificationsRequest.sort)

    val req = HttpRequest(root = ROOT_URL, queryParams = filters) / "classifications.json"

    handleRequest[PageResult[Classifications], SearchClassificationsResponse](req, (body, rateLimits) => SearchClassificationsResponse(body, rateLimits))
  }

  override def getClassification(getClassificationRequest: GetClassificationRequest)(implicit ec: ExecutionContext): dispatch.Future[Response[Classification]] = {
    val filters = Map[String, Filter[_]]()
      .updated("locale", getClassificationRequest.locale)

    val req = HttpRequest(root = ROOT_URL, queryParams = filters) / "classifications" / (getClassificationRequest.id + ".json")

    handleRequest[Classification, GetClassificationResponse](req, (body, rateLimits) => GetClassificationResponse(body, rateLimits))
  }
}
