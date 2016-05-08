package com.ruba.repo.errorhandling

import java.sql.SQLException

import com.ruba.IdCollisionException
import com.twitter.util.Future

/**
  * Created by rikard on 2016-03-15.
  */
object SQLExceptionHandling {

  private[repo] def createErrors[A]: PartialFunction[Throwable, Future[A]] = {
    case e: SQLException =>
      e.getErrorCode match {
        case 23505 =>
          Future.exception(IdCollisionException("Identifier not unique"))
        case _ =>
          Future.exception(e)
      }
  }

}
