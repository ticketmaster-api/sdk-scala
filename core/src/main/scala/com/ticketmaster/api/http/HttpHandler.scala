package com.ticketmaster.api.http

import java.time.{Instant, ZoneId, ZonedDateTime}

import argonaut.Argonaut._
import argonaut.Shapeless._
import argonaut._
import com.ticketmaster.api.Api._
import com.ticketmaster.api.http.protocol.{HttpRequest, HttpResponse}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}
import scalaz.{-\/, \/, \/-}


trait HttpHandler {
  val apiKey: String

  val userAgent: String

  val okRange = 200 to 299

  def http: Http = new DispatchHttp

  type ResponseHandler[B, R] = (B, RateLimits) => R

  def handleRequest[B, R](req: HttpRequest, handler: ResponseHandler[B, R])(implicit ec: ExecutionContext, decode: DecodeJson[B]) = {
    http(req
      .addQueryParameter("apikey", apiKey)
      .addHeader("User-Agent", s"${userAgent}/${build.Info.version}")
    ).map(res => handleResponse(res, handler))
  }

  private def handleResponse[B, R](response: HttpResponse, handler: ResponseHandler[B, R])(implicit decode: DecodeJson[B]): R = {
    def eitherBody = response.body.fold[String \/ String](-\/("No response body"))(b => \/-(b))

    def errorMsg(str: String) = decodeBody[Errors](str)//.map(_.toString).getOrElse(s"Failed to decode body: ${response.body.get}")

    response.status match {
      case status if okRange contains status => {
        val parts = for {
          body <- eitherBody
          decodedBody <- decodeBody[B](body)
          rateLimits <- extractRateLimits(response)
        } yield(decodedBody, rateLimits)

        val (body, rateLimits) = parts.valueOr(msg => throw new ApiException(msg))
        handler(body, rateLimits)
      }
      case 404 => {
        val parts = for {
          body <- eitherBody
          decoded <- errorMsg(body)
        } yield(decoded.toString)

        throw new ResourceNotFoundException(parts.valueOr(identity))
      }
      case _ => {
        val parts = for {
          body <- eitherBody
          decoded <- errorMsg(body)
        } yield(decoded.toString)

        throw new ApiException(parts.valueOr(identity))
      }
    }
  }

  private def decodeBody[T](json: String)(implicit decode: DecodeJson[T]): \/[String, T] = Parse.decodeEither[T](json)

  private def extractRateLimits(response: HttpResponse) = {
    def extract[T](t: => T) = {
      Try(t) match {
        case Success(i) => \/-(i)
        case Failure(e) => -\/("Missing rate limit")
      }
    }

    for {
      rateLimit <- extract(response.headers("Rate-Limit").toInt)
      rateLimitAvailable <- extract(response.headers("Rate-Limit-Available").toInt)
      rateLimitOver <- extract(response.headers("Rate-Limit-Over").toInt)
      rateLimitReset <- extract(response.headers("Rate-Limit-Reset").toLong)
    } yield {
      RateLimits(rateLimit,
        rateLimitAvailable,
        rateLimitOver,
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(rateLimitReset), ZoneId.of("UTC")))
    }
  }
}
