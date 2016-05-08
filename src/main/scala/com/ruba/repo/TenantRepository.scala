package com.ruba.repo

import com.ruba.api.Tenant
import com.ruba.repo.tables.{DbTenant, TenantsTable, DbComponent}
import com.ruba.util.FutureConversion.ScalaFutureExtention
import com.twitter.util.Future

/**
  * Created by rikard on 2016-02-25.
  */
trait TenantRepository {
  def tenants: Future[Seq[Tenant]]
  def tenant(id: String): Future[Option[Tenant]]
  def create(tenant: Tenant): Future[Unit]
  def update(tenantId: String, tenant: Tenant): Future[Unit]
}

trait ITenantRepository extends TenantRepository with TenantsTable { self: DbComponent =>

  import driver.api._
  import TenantConversions._
  import scala.concurrent.ExecutionContext.Implicits.global
  import errorhandling.SQLExceptionHandling._

  override def tenants: Future[Seq[Tenant]] = {
    db.run[Seq[DbTenant]] {
      tenantsQ.result
    }.map(_.map(toTenant)).toTwitterFuture
  }

  override def tenant(id: String): Future[Option[Tenant]] = {
    db.run[Option[DbTenant]] {
      tenantsQ.filter(_.id === id).result.headOption
    }.map(_.map(toTenant)).toTwitterFuture
  }

  override def create(tenant: Tenant): Future[Unit] = {
    db.run[String] {
      createTenantQ(toDbTenant(tenant)).map(_ => tenant.id)
    }.toTwitterFuture
      .rescue(createErrors)
      .unit
  }

  override def update(tenantId: String, tenant: Tenant): Future[Unit] = {
    db.run[Int] {
      updateTenantQ(tenantId, toDbTenant(tenant.copy(id = tenantId)))
    }.toTwitterFuture.unit
  }
}

object TenantConversions {
  def toDbTenant(tenant: Tenant): DbTenant = DbTenant(tenant.id, tenant.name)
  def toTenant(dbTenant: DbTenant): Tenant = Tenant(dbTenant.id, dbTenant.name)
}

