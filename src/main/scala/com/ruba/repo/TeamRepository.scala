package com.ruba.repo

import java.util.UUID

import com.ruba.api.Team
import com.ruba.repo.tables._
import com.twitter.util.Future

/**
  * Created by rikard on 2016-02-25.
  */
trait TeamRepository extends {
  def teamsFor(tenantId: String): Future[Seq[Team]]
  def teamWith(id: String)(tenantId: String): Future[Option[Team]]
  def create(team: Team)(tenantId: String): Future[String]
  def update(teamId: String, team: Team)(tenantId: String): Future[Unit]
}

trait ITeamRepository extends TeamRepository with TeamsTable with UsersTeamsTable { self: DbComponent =>

  import driver.api._
  import com.ruba.util.FutureConversion.ScalaFutureExtention
  import TeamConversions._
  import scala.concurrent.ExecutionContext.Implicits.global

  override def teamsFor(tenantId: String): Future[Seq[Team]] = {
    db.run[TeamsQueryResponse] {
      val teamsResult = teamsForTenantQ(tenantId).result
      for {
        team <- teamsResult
        users <- usersFor(team)
      } yield (team, users)
    }.map(mergeTeamsResponse)
      .toTwitterFuture
  }

  override def teamWith(id: String)(tenantId: String): Future[Option[Team]] = {
    db.run[TeamsQueryResponse] {
      val teamsResult = teamWithIdQ(tenantId)(id).result
      for {
        team <- teamsResult
        users <- usersFor(team)
      } yield (team, users)
    }.map(mergeTeamsResponse)
      .map(_.headOption)
      .toTwitterFuture
  }

  override def create(team: Team)(tenantId: String): Future[String] = {
    db.run[String] {
      val dbTeam = toDbTeamWithId(team, tenantId)
      createTeamQ(dbTeam) >>
        setUsersForTeamQ(dbTeam.id, team.memberIds)
          .map(_ => dbTeam.id)
    }.toTwitterFuture
  }

  override def update(teamId: String, team: Team)(tenantId: String): Future[Unit] = {
    db.run[Option[Int]] {
      updateTeamQ(teamId, toDbTeam(team, tenantId)) >>
        setUsersForTeamQ(teamId, team.memberIds)
    }.toTwitterFuture.unit
  }

  private def usersFor(teams: Seq[DbTeam]) = {
    DBIO.sequence(
      teams.map { team =>
        usersForTeamQ(team.id).result.map((team, _))
      }
    )
  }

  type TeamsQueryResponse = (Seq[DbTeam], Seq[(DbTeam, Seq[DbUser])])

  private def mergeTeamsResponse(response: TeamsQueryResponse): Seq[Team] = {
    response match {
      case (teams, teamsUsers) =>
        val users = teamsUsers.toMap.withDefaultValue(Seq.empty)
        teams.map { team =>
          toTeam(team, users(team))
        }
    }
  }
}

object TeamConversions {

  def toTeam(dbTeam: DbTeam, users: Seq[DbUser]): Team = {
    Team(
      id = Some(dbTeam.id),
      name = dbTeam.name,
      imageUrl = dbTeam.imageUrl,
      email = dbTeam.email,
      description = dbTeam.description,
      ambassadorId = dbTeam.ambassador,
      memberIds = users.map(_.id)
    )
  }

  def toDbTeam(team: Team, tenantId: String): DbTeam = {
    team.id match {
      case Some(id) =>
        DbTeam(
          id = id,
          tenant = tenantId,
          name = team.name,
          imageUrl = team.imageUrl,
          email = team.email,
          description = team.description,
          ambassador = team.ambassadorId
        )
      case None => toDbTeamWithId(team, tenantId)
    }
  }

  def toDbTeamWithId(team: Team, tenantId: String): DbTeam = {
    toDbTeam(
      team = team.copy(id = Some(UUID.randomUUID().toString)),
      tenantId = tenantId
    )
  }
}
