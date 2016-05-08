package com.ruba.util

import com.twitter.bijection.twitter_util.UtilBijections
import com.twitter.util.{Future => TFuture}

import scala.concurrent.{Future => SFuture}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by rikard on 2016-01-17.
  */
object FutureConversion {
  implicit class ScalaFutureExtention[A](sFuture: SFuture[A]) {
    def toTwitterFuture: TFuture[A] = {
      scala2TwitterFut(sFuture)
    }
  }


  implicit def scala2TwitterFut[A](future: SFuture[A]): TFuture[A] = {
    UtilBijections.twitter2ScalaFuture[A].invert(future)
  }

  def option2Future[A](option: Option[A])(exc: => Throwable): TFuture[A] = {
    option match {
      case Some(a)  =>  TFuture.value(a)
      case None     =>  TFuture.exception(exc)
    }
  }

  implicit class OptionFutures[A](option: Option[A]) {
    def futureOr(t: Throwable): TFuture[A] = {
      option2Future(option)(t)
    }
  }
}

