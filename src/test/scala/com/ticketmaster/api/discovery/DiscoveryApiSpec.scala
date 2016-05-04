package com.ticketmaster.api.discovery

import org.scalatest.{FlatSpec, Matchers}

import scala.language.postfixOps

class DiscoveryApiSpec extends FlatSpec with Matchers {

  val apiKey = "12345"

  behavior of "discovery API"

  it should "create discovery api client" in {
    val api = DiscoveryApi(apiKey)
    api.apiKey should be(apiKey)
  }
}
