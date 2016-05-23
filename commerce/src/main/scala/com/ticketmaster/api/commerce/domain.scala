package com.ticketmaster.api.commerce


object domain {
  case class EventOffers(limits: Limits)

  case class Limits(max: Int)

}
