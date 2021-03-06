package com.ticketmaster.api.discovery

object domain {
  case class Events(events: Seq[Event])

  case class Event(name: String,
                   `type`: String,
                   id: String,
                   test: Boolean,
                   locale: String,
                   url: String,
                   pleaseNote: Option[String],
                   priceRanges: Option[Seq[PriceRange]],
                   promoter: Option[Promoter],
                   info: Option[String],
                   images: Seq[Image],
                   sales: Sales,
                   dates: Dates,
                   classifications: Option[Seq[EventClassification]])

  case class EventImages(`type`: String,
                         id: String,
                         images: Seq[Image])

  case class Attractions(attractions: Seq[Attraction])

  case class Attraction(id: String,
                        name: String)

  case class Venues(venues: Seq[Venue])

  case class Venue(id: String,
                   name: String)

  case class Classifications(classifications: Seq[Classification])

  case class Classification(segment: Segment)

  case class EventClassification(primary: Boolean,
                                 segment: Segment,
                                 genre: Genre,
                                 subgenre: Genre)

  case class PriceRange(`type`: String,
                        currency: String,
                        min: BigDecimal,
                        max: BigDecimal)

  case class Promoter(id: String)

  case class Dates(start: Date,
                   timezone: Option[String],
                   status: Status)

  case class Date(dateTime: Option[String],
                  localDate: String,
                  localTime: Option[String],
                  dateTBD: Boolean,
                  dateTBA: Boolean,
                  timeTBA: Boolean,
                  noSpecificTime: Boolean)

  case class Status(code: Option[String])

  case class Image(ratio: Option[String], url: String, width: Int, height: Int, fallback: Boolean)

  case class Sales(public: PublicSales)

  case class PublicSales(startDateTime: Option[String],
                         startTBD: Boolean,
                         endDateTime: Option[String])

  case class Genre(id: String, name: String)

  case class Segment(id: String,
                     name: String)
}
