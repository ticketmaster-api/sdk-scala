package com.ticketmaster.api.discovery

import org.scalatest.{Matchers, FlatSpec}

import scala.language.postfixOps

class DiscoverySpec extends FlatSpec with Matchers {

  val apiKey = "12345"

  behavior of "discovery client"

  it should "create discovery client" in {
    val api = Discovery(apiKey)
    api.apiKey should be(apiKey)
  }
}
