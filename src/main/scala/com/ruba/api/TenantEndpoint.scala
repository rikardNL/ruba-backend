package com.ruba.api

import com.ruba.{IdCollisionException, TenantNotFoundException}
import com.ruba.service.TenantService
import com.twitter.util.Future
import io.finch._
import io.finch.circe._
import io.circe.generic.auto._

/**
  * Created by rikard on 2016-02-04.
  */
trait TenantEndpoint {

  val tenantService: TenantService

  val tenantsPath = "tenants"

  val tenants: Endpoint[Seq[Tenant]] = get(tenantsPath) {
    tenantService.tenants.map(Ok)
  }

  val tenantWithId: Endpoint[Tenant] = get(tenantsPath / string("id")) { id: String =>
    tenantService.tenantWithId(id).map {
      case Some(tenant) => Ok(tenant)
      case None         => NotFound(TenantNotFoundException(id))
    }
  }

  val create: Endpoint[Unit] = post(tenantsPath ? body.as[Tenant]) { tenant: Tenant =>
    tenantService.create(tenant).map(Created).rescue {
      case error: IdCollisionException => Future.value(BadRequest(error))
    }
  }

  val update: Endpoint[Unit] = put(tenantsPath / string("id") ? body.as[Tenant]) { (id: String, tenant: Tenant) =>
    tenantService.update(id, tenant).map(Ok)
  }

  def endpoint = tenants :+: tenantWithId :+: create :+: update
}

case class Tenant(id: String,
                  name: String)