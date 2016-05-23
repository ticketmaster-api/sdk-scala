package com.ticketmaster.api.commerce


object Commerce {
  def apply(key: String) = new HttpCommerceApi {
    override val apiKey: String = key
  }
}
