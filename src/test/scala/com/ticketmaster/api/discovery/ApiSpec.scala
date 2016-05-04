package com.ticketmaster.api.discovery

import java.net.{URL => JavaUrl}

import com.ning.http.client.{Response => NingResponse}
import dispatch.{HttpExecutor, Req}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers, Suite}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

trait ApiSpec extends FlatSpec with Matchers with ScalaFutures with MockFactory {
  this: Suite =>
}

trait HttpDsl {
  this: ApiSpec =>

  sealed case class MockResponseCriteria(status: Int = 200, headers: Map[String, String] = Map.empty, maybeBody: Option[String] = None) {
    def withStatus(newStatus: Int) = this.copy(newStatus)

    def withHeaders(newHeaders: Map[String, String]) = this.copy(headers = newHeaders)

    def withBody(newBody: String) = this.copy(maybeBody = Some(newBody))
  }

  object MockResponseCriteria {
    implicit def buildResponse(criteria: MockResponseCriteria) = {
      val response = mock[NingResponse]
      (response.getStatusCode _: () => Int).expects().returning(criteria.status)
      criteria.headers.foreach(hdr => (response.getHeader _: String => String).expects(hdr._1).returning(hdr._2))
      if (criteria.maybeBody.isDefined) {
        (response.getResponseBody _: () => String).expects().returning(criteria.maybeBody.get)
      }
      response
    }
  }

  def mockResponse = MockResponseCriteria()

  sealed case class Url(protocol: String, host: String, port: Int, path: String, queryParams: Map[String, String])

  object Url {
    def apply(url: JavaUrl): Url = {
      Url(url.getProtocol,
        url.getHost,
        url.getPort,
        url.getPath,
        queryParamsAsMap(url.getQuery))
    }

    private def queryParamsAsMap(query: String) = Map(query.split("&").map(qp => {
      val kv = qp.split("=")
      kv(0) -> kv(1)
    }): _*)
  }

  type RequestMatcher = (Req, ExecutionContext) => Boolean

  val anything: RequestMatcher = (r: Req, ec: ExecutionContext) => true

  def requestMatcher(expectedUrl: String): RequestMatcher = (req: Req, executionContext: ExecutionContext) => {
    val expected = Url(new JavaUrl(expectedUrl))
    val actual = Url(new JavaUrl(req.toRequest.getRawUrl))

    if (expected != actual) {
      println(s"${req.toRequest.getRawUrl} does not equal expected ${expectedUrl}")
    }
    expected == actual && req.toRequest.getHeaders.getFirstValue("User-Agent").startsWith("Ticketmaster Discovery Scala")
  }

  def mockHttp = {
    new HttpPendingExpects(mock[HttpExecutor])
  }

  class HttpPendingExpects(http: HttpExecutor) {
    def expects(requestMatcher: RequestMatcher) = {
      new HttpPendingResponse(http, requestMatcher)
    }
  }

  class HttpPendingResponse(http: HttpExecutor, requestMatcher: RequestMatcher) {
    def returns(response: NingResponse) = {
      (http.apply(_: Req)(_: ExecutionContext))
        .expects(where {
          requestMatcher
        }).returns(Future.successful(response))
      http
    }
  }

}