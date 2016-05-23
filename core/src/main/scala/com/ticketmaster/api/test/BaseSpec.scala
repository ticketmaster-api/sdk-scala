package com.ticketmaster.api.test

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers, Suite}
import org.scalatest.concurrent.ScalaFutures

import scala.language.implicitConversions

trait BaseSpec extends FlatSpec with Matchers with ScalaFutures with MockFactory {
  this: Suite =>
}
