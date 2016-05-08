package com.ruba.repo.tables

import java.util.UUID

/**
  * Created by rikard on 2016-01-28.
  */
case class DbTenant(id: String, name: String)

private[repo] trait TenantsTable extends DbComponent {

  import driver.api._

  class Tenants(tag: Tag) extends Table[DbTenant](tag, "Tenants") {

    def id    =   column[String]("id", O.PrimaryKey)
    def name  =   column[String]("name")

    override def * = (id, name) <> (DbTenant.tupled, DbTenant.unapply)
  }

  protected val tenantsTable = TableQuery[Tenants]

  protected def tenantsQ = {
    tenantsTable.to[Seq]
  }

  protected def tenantQ(id: String) = {
    tenantsQ.filter(_.id === id)
  }

  protected def updateTenantQ(id: String, tenant: DbTenant) = {
    tenantsTable.insertOrUpdate(tenant)
  }

  protected def createTenantQ(tenant: DbTenant) = {
    tenantsTable += tenant
  }
}
