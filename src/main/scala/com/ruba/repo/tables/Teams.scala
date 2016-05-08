package com.ruba.repo.tables

/**
  * Created by rikard on 2016-01-28.
  */
case class DbTeam(id: String,
                  tenant: String,
                  name: String,
                  imageUrl: Option[String],
                  email: Option[String],
                  description: Option[String],
                  ambassador: Option[String])

private[repo] trait TeamsTable extends TenantsTable with UsersTable { self: DbComponent =>
  import driver.api._

  class Teams(tag: Tag) extends Table[DbTeam](tag, "Teams") {
    def id            =   column[String]("id", O.PrimaryKey)
    def tenant        =   column[String]("tenant")
    def name          =   column[String]("name")
    def email         =   column[Option[String]]("email")
    def imageUrl      =   column[Option[String]]("image_url")
    def description   =   column[Option[String]]("description")
    def ambassador    =   column[Option[String]]("ambassador")

    def ambassadorFk  =   foreignKey("ambassador_fk", ambassador, usersTable)(_.id)
    def tenantFk      =   foreignKey("team_tenant_fk", tenant, tenantsTable)(_.id)

    override def * = (id, tenant, name, imageUrl, email, description, ambassador) <> (DbTeam.tupled, DbTeam.unapply)
  }

  protected val teamsTable = TableQuery[Teams]

  protected def teamsForTenantQ(tenantId: String) = {
    teamsTable.filter(_.tenant === tenantId)
  }

  protected def teamWithIdQ(tenantId: String)(id: String) = {
    teamsForTenantQ(tenantId).filter(_.id === id)
  }

  protected def updateTeamQ(id: String, team: DbTeam) = {
    teamsTable.insertOrUpdate(team.copy(id = id))
  }

  protected def createTeamQ(team: DbTeam) = {
    teamsTable += team
  }

  protected def ambassadorForTeams(tenantId: String)(userId: String) = {
    teamsForTenantQ(tenantId).filter {
      _.ambassador
        .map(_ === userId)
        .getOrElse(false)
    }
  }
}
