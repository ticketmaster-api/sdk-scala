package com.ticketmaster.api

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import scalaz._
import Scalaz._

object Api {
  sealed trait Filter[+T] {
    def isDefined: Boolean

    def value: T
  }

  object Filter {
    final case object NoFilter extends Filter[Nothing] {
      override def isDefined: Boolean = false

      override def value: Nothing = throw new NoSuchElementException()
    }

    final case class Filtered[T](value: T) extends Filter[T] {
      override def isDefined: Boolean = true
    }

    implicit def stringToValue(s: String) = Filtered(s)

    implicit def intToValue(i: Int) = Filtered(i)

    implicit def seqToValue[T](s: Seq[T]) = Filtered(s)

    implicit def dateTimeToValue[T](t: ZonedDateTime) = Filtered(t)

    implicit def stringifyQueryParams(queryParamMap: Map[String, Filter[_]]): Map[String, String] = {
      val stringify: PartialFunction[Any, String] = {
        case z: ZonedDateTime => z.format(DateTimeFormatter.ISO_INSTANT)
        case s: Seq[_] => s.mkString(",")
        case a => a.toString
      }

      queryParamMap.filter { f: (String, Filter[_]) => f._2.isDefined }
        .map(d => (d._1, stringify(d._2.value)))
    }
  }

  //todo may want to generalise requests - by id and by page
  trait PageRequest {
    def size: Filter[Int]

    def page: Filter[Int]

    def sort: Filter[String]
  }

  case class RateLimits(rateLimit: Int,
                        available: Int,
                        over: Int,
                        reset: ZonedDateTime)

  case class Page(size: Int,
                  totalElements: Int,
                  totalPages: Int,
                  number: Int)

  case class Link(href: String, templated: Option[Boolean])

  case class Links(self: Link)

  //todo this could be a stream, stream iterator
  case class PageResult[T](_embedded: T, page: Page, _links: Links)

  trait Response[T] {
    def result: T

    def rateLimits: RateLimits
  }

  trait PageResponse[T] {
    def pageResult: PageResult[T]

    def rateLimits: RateLimits
  }

  case class Error(code: String, detail: String, status: Int)

  case class Errors(errors: Seq[Error])

  class ApiException(message: String) extends RuntimeException(message)

  class ResourceNotFoundException(message: String) extends ApiException(message)

  class DecodeException(message: String) extends ApiException(message)
}
