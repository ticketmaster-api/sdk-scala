package com.ticketmaster.api.discovery

import com.ticketmaster.api.http.Http
import com.ticketmaster.api.http.protocol.{HttpResponse, HttpRequest}
import org.scalamock.scalatest.MockFactory
import org.scalatest.Suite

import scala.concurrent.{ExecutionContext, Future}

trait TestableDiscoveryApi extends MockFactory {
  this: Suite =>

  val testApiKey: String

  def testableApi(expectedRequest: HttpRequest, response: HttpResponse) = {
    new HttpDiscoveryApi() {
      override val apiKey: String = testApiKey

      override def http: Http = {
        val mockHttp = mock[Http]
        (mockHttp.apply(_: HttpRequest)(_: ExecutionContext))
          .expects(expectedRequest.addHeader("User-Agent", "Ticketmaster Discovery Scala/0.1.0-SNAPSHOT"), *)
          .returning(Future.successful(response))
        mockHttp
      }
    }
  }
}