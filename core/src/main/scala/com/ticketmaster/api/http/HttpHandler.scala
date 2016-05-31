package com.ticketmaster.api.http

import java.time.{Instant, ZoneId, ZonedDateTime}

import argonaut.Argonaut._
import argonaut.Shapeless._
import argonaut._
import com.ticketmaster.api.Api._
import com.ticketmaster.api.http.protocol.{HttpRequest, HttpResponse}

import scala.concurrent.ExecutionContext
import scalaz._


trait HttpHandler {
  val apiKey: String

  val USER_AGENT: String

  val okRange = 200 to 299

  def http: Http = new DispatchHttp

  type ResponseHandler[B, R] = (B, RateLimits) => R

  def handleRequest[B, R](req: HttpRequest, handler: ResponseHandler[B, R])(implicit ec: ExecutionContext, decode: DecodeJson[B]) = {
    http(req
      .addQueryParameter("apikey", apiKey)
      .addHeader("User-Agent", s"${USER_AGENT}/${build.Info.version}")
    ).map(res => handleResponse(res, handler))
  }

  private def handleResponse[B, R](response: HttpResponse, handler: ResponseHandler[B, R])(implicit decode: DecodeJson[B]): R = {
    def errors = decodeEither[Errors](response.body.get).map(_.toString).getOrElse(s"Failed to decode body: ${response.body.get}")

    response.status match {
      case status if okRange contains status => {
        val parts = for {
          decoded <- decodeEither[B](response.body.get)
          rateLimits <- extractRateLimitInfo(response)
        } yield (decoded, rateLimits)

        parts.map(right => handler(right._1, right._2)) | (throw new ApiException("Failed to read response"))
      }
      case 404 => throw new ResourceNotFoundException(errors)
      case _ => throw new ApiException(errors)
    }
  }

  private def extractRateLimitInfo(response: HttpResponse) = {
    \/-(RateLimits(
      response.headers("Rate-Limit").toInt,
      response.headers("Rate-Limit-Available").toInt,
      response.headers("Rate-Limit-Over").toInt,
      ZonedDateTime.ofInstant(Instant.ofEpochMilli(response.headers("Rate-Limit-Reset").toLong), ZoneId.of("UTC"))))
  }

  private def decodeEither[T](json: String)(implicit decode: DecodeJson[T]): \/[String, T] = Parse.decodeEither[T](json)
}
