package com.ruba.api

import com.ruba.TeamNotFoundException
import com.ruba.service.{TenantService, TeamService}
import io.finch._
import io.finch.circe._
import io.circe.generic.auto._

/**
  * Created by rikard on 2016-02-04.
  */
trait TeamEndpoint extends ApiHelpers {

  val tenantService: TenantService
  val teamService: TeamService

  val tenantTeams = string("tenant") / "teams"

  val teams: Endpoint[Seq[Team]] = get(tenantTeams) { tenantId: String =>
    withTenant(tenantId) { tenant =>
      teamService.teams(tenant).map(Ok)
    }
  }

  val teamWithId: Endpoint[Team] = get(tenantTeams / string("teamId")) { (tenantId: String, teamId: String) =>
    withTenant(tenantId) { tenant =>
      teamService.teamWithId(tenant)(teamId).map {
        case Some(team) => Ok(team)
        case None       => NotFound(TeamNotFoundException(teamId))
      }
    }
  }

  val create: Endpoint[String] = post(tenantTeams ? body.as[Team]) { (tenantId: String, team: Team) =>
    withTenant(tenantId) { tenant =>
      teamService.create(tenant)(team).map(Created)
    }
  }

  val update: Endpoint[Unit] = put(tenantTeams / string("teamId") ? body.as[Team]) {
    (tenantId: String, teamId: String, team: Team) =>
      withTenant(tenantId) { tenant =>
        teamService.update(tenant)(teamId, team).map(Ok)
      }
  }

  def endpoint = teams :+: teamWithId :+: create :+: update
}

case class Team(id: Option[String],
                name: String,
                imageUrl: Option[String],
                email: Option[String],
                description: Option[String],
                ambassadorId: Option[String],
                memberIds: Seq[String])
