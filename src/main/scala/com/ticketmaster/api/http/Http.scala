package com.ticketmaster.api.discovery.http

import com.ticketmaster.api.discovery.http.protocol._

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConverters.mapAsScalaMapConverter
import scala.concurrent.{ExecutionContext, Future}

object protocol {
  case class HttpRequest(root: String, path: Seq[String] = Seq.empty, queryParams: Map[String, String] = Map.empty, headers: Map[String, String] = Map.empty, body: Option[String] = None) {

    def addPathSegment(segment: String) = copy(path = path :+ segment)

    def /(segment: String) = addPathSegment(segment)

    def addHeader(key: String, value: String) = copy(headers = headers.updated(key, value))

    def addQueryParameter(key: String, value: String) = copy(queryParams = queryParams.updated(key, value))
  }

  case class HttpResponse(status: Int, body: Option[String] = None, headers: Map[String, String] = Map.empty)
}

trait Http {
  def apply(request: HttpRequest)(implicit ec: ExecutionContext): Future[HttpResponse]
}

class DispatchHttp extends Http {
  override def apply(request: HttpRequest)(implicit ec: ExecutionContext): Future[HttpResponse] = {
    import dispatch._

    def addPath(req: Req, path: Seq[String]): Req = {
      path match {
        case head :: tail => addPath(req / head, tail)
        case Nil => req
      }
    }

    val req = addPath(url(request.root), request.path).setHeaders(request.headers.map(h => ((h._1, Seq(h._2))))) <<? request.queryParams

    Http(req).map(res => {
      val responseHeaders = res.getHeaders.asScala.mapValues(jl => jl.head).toMap
      HttpResponse(status = res.getStatusCode, headers = responseHeaders, body = Some(res.getResponseBody))
    })
  }
}