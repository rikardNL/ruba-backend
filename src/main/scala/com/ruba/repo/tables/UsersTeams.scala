package com.ruba.repo.tables

/**
  * Created by rikard on 2016-01-28.
  */
case class DbUsersTeams(user: String, team: String)

private[repo] trait UsersTeamsTable extends UsersTable with TeamsTable { self: DbComponent =>

  import driver.api._

  class UsersTeams(tag: Tag) extends Table[DbUsersTeams](tag, "UsersTeams") {
    def user = column[String]("user")
    def team = column[String]("team")

    def key = primaryKey("user_team_pk", (user, team))

    def userFk = foreignKey("ut_user_fk", user, usersTable)(_.id)
    def teamFk = foreignKey("team_fk", team, teamsTable)(_.id)

    override def * = (user, team) <> (DbUsersTeams.tupled, DbUsersTeams.unapply)
  }

  protected val usersTeamsTable = TableQuery[UsersTeams]

  protected def usersForTeamQ(teamId: Rep[String]) = {
    usersTeamsTable
      .filter(_.team === teamId)
      .flatMap(_.userFk)
  }

  protected def teamsForUserQ(userId: Rep[String]) = {
    usersTeamsTable
      .filter(_.user === userId)
      .flatMap(_.teamFk)
  }

  protected def addUsersForTeamQ(teamId: String, userIds: Seq[String]) = {
    usersTeamsTable ++= userIds.map(DbUsersTeams(_, teamId))
  }

  protected def addTeamsForUserQ(userId: String, teamIds: Seq[String]) = {
    usersTeamsTable ++= teamIds.map(DbUsersTeams(userId, _))
  }

  protected def deleteTeamsForUserQ(userId: String) = {
    usersTeamsTable
      .filter(_.user === userId)
      .delete
  }

  protected def deleteUsersForTeamQ(teamId: String) = {
    usersTeamsTable
      .filter(_.team === teamId)
      .delete
  }

  protected def setTeamsForUserQ(userId: String, teamIds: Seq[String]) = {
    deleteTeamsForUserQ(userId) >> addTeamsForUserQ(userId, teamIds)
  }

  protected def setUsersForTeamQ(teamId: String, userIds: Seq[String]) = {
    deleteUsersForTeamQ(teamId) >> addUsersForTeamQ(teamId, userIds)
  }
}