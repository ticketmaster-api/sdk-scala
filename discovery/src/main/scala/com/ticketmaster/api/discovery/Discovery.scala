package com.ticketmaster.api.discovery

object Discovery {
  def apply(key: String) = new HttpDiscoveryApi {
    override val apiKey: String = key
  }
}
