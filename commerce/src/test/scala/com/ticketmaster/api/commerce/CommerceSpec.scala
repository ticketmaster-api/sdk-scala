package com.ticketmaster.api.commerce

import org.scalatest.{FlatSpec, Matchers}


class CommerceSpec extends FlatSpec with Matchers {

  val apiKey = "12345"

  behavior of "commerce client"

  it should "create commerce client" in {
    val api = Commerce(apiKey)
    api.apiKey should be(apiKey)
  }
}
