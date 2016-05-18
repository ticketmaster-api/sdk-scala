package com.ticketmaster.api.discovery

import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers, Suite}

import scala.language.implicitConversions

trait BaseSpec extends FlatSpec with Matchers with ScalaFutures with MockFactory {
  this: Suite =>
}
