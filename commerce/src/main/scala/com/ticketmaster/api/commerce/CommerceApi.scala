package com.ticketmaster.api.commerce

import com.ticketmaster.api.Api.{RateLimits, Response}
import com.ticketmaster.api.commerce.domain.EventOffers

import scala.concurrent.{ExecutionContext, Future}

trait CommerceApi {
  def getEventOffers(getEventOffersRequest: GetEventOffersRequest)(implicit ec: ExecutionContext): Future[Response[EventOffers]]
}

case class GetEventOffersRequest(id: String)

case class GetEventOffersResponse(result: EventOffers,
                                  rateLimits: RateLimits) extends Response[EventOffers]