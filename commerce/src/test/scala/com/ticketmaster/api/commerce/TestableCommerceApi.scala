package com.ticketmaster.api.commerce

import com.ticketmaster.api.http.Http
import com.ticketmaster.api.http.protocol.{HttpRequest, HttpResponse}
import org.scalamock.scalatest.MockFactory
import org.scalatest.Suite

import scala.concurrent.{ExecutionContext, Future}

trait TestableCommerceApi extends MockFactory {
  this: Suite =>

  val testApiKey: String

  def testableApi(expectedRequest: HttpRequest, response: HttpResponse) = {
    new HttpCommerceApi() {
      override val apiKey: String = testApiKey

      override def http: Http = {
        val mockHttp = mock[Http]
        (mockHttp.apply(_: HttpRequest)(_: ExecutionContext))
          .expects(expectedRequest.addHeader(s"User-Agent", s"Ticketmaster Commerce Scala/${build.Info.version}"), *)
          .returning(Future.successful(response))
        mockHttp
      }
    }
  }
}