package com.ruba

import com.twitter.finagle.http.path.{Root, Path, /, ->}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.http.{Status, Method, Request, Response}
import com.twitter.io.Buf
import com.twitter.util.Future

/**
  * Created by rian on 10/01/16.
  */
class PingFilter extends SimpleFilter[Request, Response] {
  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    request.method -> Path(request.path) match {
      case Method.Get -> Root / "ping" =>
        val resp = Response(Status.Ok)
        resp.content = Buf.Utf8("pong")
        Future.value(resp)
      case _ => service(request)
    }
  }
}

object PingFilter {
  def apply(): PingFilter = new PingFilter
}
