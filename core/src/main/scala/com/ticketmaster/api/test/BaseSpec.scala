package com.ticketmaster.api.test

import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, Matchers, Suite}

import scala.concurrent.duration._
import scala.language.implicitConversions

trait BaseSpec extends FlatSpec with Matchers with ScalaFutures with MockFactory {
  this: Suite =>

  override implicit val patienceConfig = PatienceConfig(2 seconds, 200 millis)
}
