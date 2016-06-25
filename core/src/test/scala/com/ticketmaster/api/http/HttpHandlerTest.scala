package com.ticketmaster.api.http

import java.time.ZonedDateTime

import argonaut.Shapeless._
import com.ticketmaster.api.Api._
import com.ticketmaster.api.http.protocol.{HttpRequest, HttpResponse}
import com.ticketmaster.api.test.BaseSpec
import org.scalamock.scalatest.MockFactory
import org.scalatest.Suite

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}


trait TestableHttpHandler extends MockFactory {
  this: Suite =>

  def newHttpHandler(expectedRequest: HttpRequest, response: HttpResponse) = {
    new HttpHandler {
      override val apiKey: String = "12345"
      override val userAgent: String = "Ticketmaster Test Scala"

      override def http: Http = {
        val mockHttp = mock[Http]
        (mockHttp.apply(_: HttpRequest)(_: ExecutionContext))
          .expects(expectedRequest, *)
          .returning(Future.successful(response))
        mockHttp
      }
    }
  }
}

case class SomeBody(name: String)

case class SomeResponse(body: SomeBody, rateLimits: RateLimits)

class HttpHandlerTest extends BaseSpec with TestableHttpHandler {

  val responseHeaders = Map("Rate-Limit" -> "5000",
    "Rate-Limit-Available" -> "5000",
    "Rate-Limit-Over" -> "0",
    "Rate-Limit-Reset" -> "1453180594367")

  behavior of "http handler"

  it should "handle valid request" in {
    val expectedHttpRequest = HttpRequest("https://app.ticketmaster.com/", queryParams = Map("apikey" -> "12345"))
      .addHeader(s"User-Agent", s"Ticketmaster Test Scala/${build.Info.version}")
    val httpResponse = HttpResponse(200, body = Some("""{"name" : "max"}"""), headers = responseHeaders)

    val handler = newHttpHandler(expectedHttpRequest, httpResponse)

    val actualHttpRequest = HttpRequest("https://app.ticketmaster.com/")
    val responseHandler = (body: SomeBody, rateLimits: RateLimits) => SomeResponse(body, rateLimits)
    val pendingResponse = handler.handleRequest[SomeBody, SomeResponse](actualHttpRequest, responseHandler)

    whenReady(pendingResponse) { r =>
      r.body should be(SomeBody("max"))
      r.rateLimits should be(RateLimits(5000, 5000, 0, ZonedDateTime.parse("2016-01-19T05:16:34.367Z[UTC]")))
    }
  }

  it should "handle missing rate limits response headers" in {
    val expectedHttpRequest = HttpRequest("https://app.ticketmaster.com/", queryParams = Map("apikey" -> "12345"))
      .addHeader(s"User-Agent", s"Ticketmaster Test Scala/${build.Info.version}")
    val httpResponse = HttpResponse(200, body = Some("""{"name" : "max"}"""))

    val handler = newHttpHandler(expectedHttpRequest, httpResponse)

    val actualHttpRequest = HttpRequest("https://app.ticketmaster.com/")
    val responseHandler = (body: SomeBody, rateLimits: RateLimits) => SomeResponse(body, rateLimits)

    val pendingResponse = handler.handleRequest[SomeBody, SomeResponse](actualHttpRequest, responseHandler)

    whenReady(pendingResponse.failed) { t =>
      t shouldBe a[ApiException]
      t.getMessage should be("Missing rate limit")
    }
  }

  it should "handle missing json response body" in {
    val expectedHttpRequest = HttpRequest("https://app.ticketmaster.com/", queryParams = Map("apikey" -> "12345"))
      .addHeader(s"User-Agent", s"Ticketmaster Test Scala/${build.Info.version}")
    val httpResponse = HttpResponse(200)

    val handler = newHttpHandler(expectedHttpRequest, httpResponse)

    val actualHttpRequest = HttpRequest("https://app.ticketmaster.com/")
    val responseHandler = (body: SomeBody, rateLimits: RateLimits) => SomeResponse(body, rateLimits)

    val pendingResponse = handler.handleRequest[SomeBody, SomeResponse](actualHttpRequest, responseHandler)

    whenReady(pendingResponse.failed) { t =>
      t shouldBe a[ApiException]
      t.getMessage should be("No response body")
    }
  }

  it should "handle invalid json response body" in {
    val expectedHttpRequest = HttpRequest("https://app.ticketmaster.com/", queryParams = Map("apikey" -> "12345"))
      .addHeader(s"User-Agent", s"Ticketmaster Test Scala/${build.Info.version}")
    val httpResponse = HttpResponse(200, body = Some("""{"person" : "max"}"""))

    val handler = newHttpHandler(expectedHttpRequest, httpResponse)

    val actualHttpRequest = HttpRequest("https://app.ticketmaster.com/")
    val responseHandler = (body: SomeBody, rateLimits: RateLimits) => SomeResponse(body, rateLimits)

    val pendingResponse = handler.handleRequest[SomeBody, SomeResponse](actualHttpRequest, responseHandler)

    whenReady(pendingResponse.failed) { t =>
      t shouldBe a[ApiException]
      t.getMessage should be("Attempt to decode value on failed cursor.: [*.--\\(name)]")
    }
  }

  it should "handle 404 response - resource not found" in {
    val expectedHttpRequest = HttpRequest("https://app.ticketmaster.com/", queryParams = Map("apikey" -> "12345"))
      .addHeader(s"User-Agent", s"Ticketmaster Test Scala/${build.Info.version}")
    val httpResponse = HttpResponse(404, body = Some(HttpHandlerTest.error404))

    val handler = newHttpHandler(expectedHttpRequest, httpResponse)

    val actualHttpRequest = HttpRequest("https://app.ticketmaster.com/")
    val responseHandler = (body: SomeBody, rateLimits: RateLimits) => SomeResponse(body, rateLimits)

    val pendingResponse = handler.handleRequest[SomeBody, SomeResponse](actualHttpRequest, responseHandler)

    whenReady(pendingResponse.failed) { t =>
      t shouldBe a[ResourceNotFoundException]
      t.getMessage should be("Errors(Vector(Error(ABC123,Resource not found with provided criteria,404)))")
    }
  }

  it should "handle 400 response - invalid query param size" in {
    val expectedHttpRequest = HttpRequest("https://app.ticketmaster.com/", queryParams = Map("apikey" -> "12345"))
      .addHeader(s"User-Agent", s"Ticketmaster Test Scala/${build.Info.version}")
    val httpResponse = HttpResponse(400, body = Some(HttpHandlerTest.error400))

    val handler = newHttpHandler(expectedHttpRequest, httpResponse)

    val actualHttpRequest = HttpRequest("https://app.ticketmaster.com/")
    val responseHandler = (body: SomeBody, rateLimits: RateLimits) => SomeResponse(body, rateLimits)

    val pendingResponse = handler.handleRequest[SomeBody, SomeResponse](actualHttpRequest, responseHandler)

    whenReady(pendingResponse.failed) { t =>
      t shouldBe a[ApiException]
      t.getMessage should be("Errors(Vector(Error(ABC123,Query param \"size\" must be between 1 to 500,400)))")
    }
  }
}

object HttpHandlerTest {
  val error404 =
    """
      |{
      |	"errors": [{
      |		"code": "ABC123",
      |		"detail": "Resource not found with provided criteria",
      |		"status": "404",
      |		"_links": {
      |			"about": {
      |				"href": "/errors.html#ABC123"
      |			}
      |		}
      |	}]
      |}
    """.stripMargin

  val error400 =
    """
      |{
      |	"errors": [{
      |		"code": "ABC123",
      |		"detail": "Query param \"size\" must be between 1 to 500",
      |		"status": "400",
      |		"_links": {
      |			"about": {
      |				"href": "/errors.html#ABC123"
      |			}
      |		}
      |	}]
      |}
    """.stripMargin
}