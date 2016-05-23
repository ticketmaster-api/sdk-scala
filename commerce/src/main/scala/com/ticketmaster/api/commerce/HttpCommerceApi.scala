package com.ticketmaster.api.commerce

import argonaut.Shapeless._
import com.ticketmaster.api.Api._
import com.ticketmaster.api.commerce.domain.EventOffers
import com.ticketmaster.api.http.HttpHandler
import com.ticketmaster.api.http.protocol.HttpRequest

import scala.concurrent.{ExecutionContext, Future}


trait HttpCommerceApi extends CommerceApi with HttpHandler {
  val ROOT_URL = "https://app.ticketmaster.com/commerce/v2"

  val USER_AGENT = "Ticketmaster Commerce Scala"

  override def getEventOffers(getEventOffersRequest: GetEventOffersRequest)(implicit ec: ExecutionContext): Future[Response[EventOffers]] = {
    val req = HttpRequest(root = ROOT_URL) / "events" / getEventOffersRequest.id / "offers.json"

    handleRequest[EventOffers, GetEventOffersResponse](req, (body, rateLimits) => GetEventOffersResponse(body, rateLimits))
  }
}
