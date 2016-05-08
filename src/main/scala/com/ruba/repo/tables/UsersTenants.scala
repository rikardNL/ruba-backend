package com.ruba.repo.tables

/**
  * Created by rikard on 2016-03-14.
  */
case class DbUserTenant(user: String, tenant: String)

private[repo] trait UsersTenantsTable extends UsersTable with TenantsTable { self: DbComponent =>
  import driver.api._

  class UsersTenants(tag: Tag) extends Table[DbUserTenant](tag, "UsersTenants") {
    def user = column[String]("user")
    def tenant = column[String]("tenant")

    def key = primaryKey("user_tenant_pk", (user, tenant))

    def userFk = foreignKey("user_tenant_fk", user, usersTable)(_.id)
    def tenantFk = foreignKey("tenant_user_fk", tenant, tenantsTable)(_.id)

    override def * = (user, tenant) <> (DbUserTenant.tupled, DbUserTenant.unapply)
  }

  protected val usersTenantsTable = TableQuery[UsersTenants]

  protected def usersForTenantQ(tenantId: Rep[String]) = {
    usersTenantsTable
      .filter(_.tenant === tenantId)
      .flatMap(_.userFk)
  }

  protected def tenantsForUserQ(userId: Rep[String]) = {
    usersTenantsTable
      .filter(_.user === userId)
      .flatMap(_.tenantFk)
  }

  protected def addUserForTenantQ(userId: String, tenantId: String) = {
    usersTenantsTable += DbUserTenant(user = userId, tenant = tenantId)
  }
}
