package com.ticketmaster.api.discovery

import com.ticketmaster.api.http.Http
import com.ticketmaster.api.http.protocol.{HttpResponse, HttpRequest}
import org.scalamock.scalatest.MockFactory
import org.scalatest.Suite

import scala.concurrent.{ExecutionContext, Future}

trait TestableHttpDiscoveryApi extends MockFactory {
  this: Suite =>

  val testApiKey: String

  def newHttpDiscoveryApi(expectedRequest: HttpRequest, response: HttpResponse) = {
    new HttpDiscoveryApi() {
      override val apiKey: String = testApiKey

      override def http: Http = {
        val mockHttp = mock[Http]
        (mockHttp.apply(_: HttpRequest)(_: ExecutionContext))
          .expects(expectedRequest.addHeader(s"User-Agent", s"Ticketmaster Discovery Scala/${build.Info.version}"), *)
          .returning(Future.successful(response))
        mockHttp
      }
    }
  }
}