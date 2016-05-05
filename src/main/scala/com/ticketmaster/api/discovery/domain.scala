package com.ticketmaster.api.discovery

object domain {

  case class Events(events: Seq[Event])

  case class Event(id: String,
                   name: String,
                   locale: String,
                   url: String,
                   promoter: Option[Promoter],
                   sales: Sales,
                   dates: Dates,
                   test: Boolean,
                   eventType: String,
                   images: Seq[Image],
                   classifications: Option[Seq[EventClassification]])

  case class EventImages(imageType: String,
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

  case class Promoter(id: String)

  case class Dates(start: Date,
                   timezone: String,
                   status: Status)

  case class Date(dateTime: Option[String],
                  localDate: String,
                  localTime: Option[String],
                  dateTDB: Boolean,
                  dateTBA: Boolean,
                  timeTBA: Boolean,
                  noSpecificTime: Boolean)

  case class Status(code: String)

  case class Image(ratio: String, url: String, width: Int, height: Int, fallback: Boolean)

  case class Sales(public: PublicSales)

  case class PublicSales(startDateTime: Option[String],
                         startTBD: Boolean,
                         endDateTime: Option[String])

  case class Genre(id: String, name: String)

  case class Segment(id: String,
                     name: String)

}
