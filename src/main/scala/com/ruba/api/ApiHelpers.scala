package com.ruba.api

import com.ruba.TenantNotFoundException
import com.ruba.service.TenantService
import com.twitter.util.Future
import io.finch._

/**
  * Created by rikard on 2016-02-14.
  */
trait ApiHelpers {

  val tenantService: TenantService

  def withTenant[T](tenantId: String)
                   (f: Tenant => Future[Output[T]]): Future[Output[T]] = {
    tenantService.tenantWithId(tenantId).flatMap {
      case Some(tenant) => f(tenant)
      case None         => Future.value(NotFound(TenantNotFoundException(tenantId)))
    }
  }

}
