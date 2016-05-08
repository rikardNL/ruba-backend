package com.ruba.service

import com.ruba.{TestData, TeamNotFoundException, TestUtil}
import com.ruba.api.Tenant

/**
  * Created by rikard on 2016-02-21.
  */
class TeamServiceSpec extends ServiceTestComponent with TestData with TestUtil  {

  class FixtureParam {
    val headTenant = testTenants.head
    val lastTenant = testTenants.last
    val headTeams = testTeams.take(2)
    val lastTeams = testTeams.slice(2, 3)
    val headTenantTeams = mutableTeams(headTeams, headTenant)
    val lastTenantTeams = mutableTeams(lastTeams, lastTenant)

    val newTeam = testTeams.last

    val teamService = new InMemoryTeamService(headTenantTeams ++ lastTenantTeams)
  }

  override protected def withFixture(test: OneArgTest) = {
    super.withFixture(test.toNoArgTest(new FixtureParam))
  }

  "TeamService" should "lists teams for a tenant" in { f =>
    val res = futureResultOrThrow {
      f.teamService.teams(f.headTenant)
    }
    res.size should be (f.headTeams.size)
    res.map(_.name) should contain theSameElementsAs f.headTeams.map(_.name)
  }

  it should "get teams for id" in { f =>
    val targetTeam = f.headTeams.head

    val res = futureResultOrThrow {
      f.teamService.teamWithId(f.headTenant)(targetTeam.id.get)
    }
    res should not be empty
    res.get should be (targetTeam)
  }

  it should "return empty for no team found" in { f =>
    val res = futureResultOrThrow {
      f.teamService.teamWithId(f.headTenant)("UNKNOWN ID")
    }
    res should be (empty)
  }

  it should "return empty for unknown tenant" in { f =>
    val resList = futureResultOrThrow {
      f.teamService.teamWithId(Tenant(id = "UNKNOWN", name = "UNKNOWN"))(f.headTeams.head.id.get)
    }
    val resSingle = futureResultOrThrow {
      f.teamService.teams(Tenant(id = "UNKNOWN", name = "UNKNOWN"))
    }
    resList should be (empty)
    resSingle should be (empty)
  }

  it should "not get teams from other tenants" in { f =>
    val res = futureResultOrThrow {
      f.teamService.teamWithId(f.lastTenant)(f.headTeams.head.id.get)
    }
    res should be (empty)
  }

  it should "create teams for tenant" in { f =>
    val before = futureResultOrThrow {
      f.teamService.teamWithId(f.headTenant)(f.newTeam.id.get)
    }
    before should be (empty)

    val id = futureResultOrThrow {
      f.teamService.create(f.headTenant)(f.newTeam)
    }
    id should not be empty
    id should not be f.newTeam.id.get

    val res = futureResultOrThrow {
      f.teamService.teamWithId(f.headTenant)(id)
    }
    res should not be empty
    res.get.name should be (f.newTeam.name)
  }

  it should "be able to delete a team" in { f =>
    val deleteTeam = f.headTeams.head

    val before = futureResultOrThrow {
      f.teamService.teamWithId(f.headTenant)(deleteTeam.id.get)
    }
    before should not be empty
    futureResultOrThrow {
      f.teamService.delete(f.headTenant)(deleteTeam.id.get)
    }

    val res = futureResultOrThrow {
      f.teamService.teamWithId(f.headTenant)(deleteTeam.id.get)
    }
    res should be (empty)

    val remaining = futureResultOrThrow {
      f.teamService.teams(f.headTenant)
    }
    remaining should not be empty
  }

  it should "handle deletion of non-existing team and non-existing tenant" in { f =>
    futureResultOrThrow {
      f.teamService.delete(f.headTenant)(f.newTeam.id.get)
    }
    futureResultOrThrow {
      f.teamService.delete(Tenant(id = "UNKNOWN", name = "UNKNOWN"))(f.headTeams.head.id.get)
    }
    val res = futureResultOrThrow {
      f.teamService.teamWithId(f.headTenant)(f.headTeams.head.id.get)
    }
    res should not be empty
  }

  it should "be able to update an existing team" in { f =>
    val targetTeam = f.headTeams.head
    val newEmail = "new@email.com"

    futureResultOrThrow {
      f.teamService.update(f.headTenant)(targetTeam.id.get, targetTeam.copy(email = Some(newEmail)))
    }
    val updated = futureResultOrThrow {
      f.teamService.teamWithId(f.headTenant)(targetTeam.id.get)
    }

    updated should not be empty
    updated.get.email should be (Some(newEmail))
  }

  it should "not change id of updated team" in { f =>
    val targetTeam = f.headTeams.head
    val newId = "NEWID"
    val newEmail = "new@email.com"

    futureResultOrThrow {
      f.teamService.update(f.headTenant)(
        id = targetTeam.id.get,
        team = targetTeam.copy(id = Some(newId), email = Some(newEmail))
      )
    }

    val res = futureResultOrThrow {
      f.teamService.teamWithId(f.headTenant)(targetTeam.id.get)
    }
    res should not be empty
    res.get should be (targetTeam.copy(email = Some(newEmail)))

    futureResultOrThrow {
      f.teamService.teamWithId(f.headTenant)(newId)
    } should be (empty)
  }

  it should "fail attempt to update non-existing user" in { f =>
    intercept[TeamNotFoundException] {
      futureResultOrThrow {
        f.teamService.update(f.headTenant)("NONEXISTINGID", f.headTeams.head)
      }
    }
  }

}
