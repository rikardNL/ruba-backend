package com.ruba.repo

import com.ruba.util.FutureConversion._
import slick.driver.H2Driver.api._

class ITeamRepositoryTest
  extends DBTestComponent
    with ITeamRepository with ITenantRepository with IUserRepository {

  val allSchemas =
    tenantsTable.schema ++
      usersTable.schema ++
      usersTenantsTable.schema ++
      teamsTable.schema ++
      usersTeamsTable.schema ++
      userImagesTable.schema

  class FixtureParam {
    futureResultOrThrow(create(tenant1))
    futureResultOrThrow(create(tenant2))
    val user1Id = futureResultOrThrow(create(user1Clean)(tenant1.id))
    val user2Id = futureResultOrThrow(create(user2Clean)(tenant1.id))
    val user3Id = futureResultOrThrow(create(user3Clean)(tenant1.id))
    val user4Id = futureResultOrThrow(create(user4Clean)(tenant1.id))
  }

  before { futureResultOrThrow(db.run(allSchemas.create)) }

  after { futureResultOrThrow(db.run(allSchemas.drop)) }

  override protected def withFixture(test: OneArgTest) = {
    super.withFixture(test.toNoArgTest(new FixtureParam))
  }

  "TeamRepository" should "get all teams for a tenant" in { f =>
    futureResultOrThrow(create(team1Clean)(tenant1.id))
    futureResultOrThrow(create(team2Clean)(tenant1.id))
    futureResultOrThrow(create(team3Clean)(tenant1.id))
    futureResultOrThrow(create(team4Clean)(tenant2.id))

    val res = futureResultOrThrow(teamsFor(tenant1.id)).map(_.name)
    res should contain allOf (team1Clean.name, team2Clean.name, team3Clean.name)
    res should not contain team4Clean.name
  }

  it should "get team by id" in { f =>
    val team1Id = futureResultOrThrow(create(team1Clean)(tenant1.id))
    futureResultOrThrow(create(team2Clean)(tenant1.id))

    val res = futureResultOrThrow(teamWith(team1Id)(tenant1.id)).map(_.name)
    res should contain (team1Clean.name)
  }

  it should "not return team by id from wrong tenant" in { f =>
    val team1Id = futureResultOrThrow(create(team1Clean)(tenant1.id))
    val res = futureResultOrThrow(teamWith(team1Id)(tenant2.id))
    res should be (empty)
  }

  it should "return empty when no team is found" in { f =>
    futureResultOrThrow(teamWith("UNKNOWN_ID")(tenant1.id)) should be (empty)
  }

  it should "set a unique id for a new team" in { f =>
    val team1Id = futureResultOrThrow(create(team1Clean)(tenant1.id))
    val team2Id = futureResultOrThrow(create(team2Clean)(tenant1.id))
    team1Id should not be team2Id
  }

  it should "set members for a team" in { f =>
    val team1Id = futureResultOrThrow(
      create(
        team1Clean.copy(memberIds = Seq(f.user1Id, f.user2Id))
      )(tenant1.id)
    )

    val team = futureResultOrThrow(teamWith(team1Id)(tenant1.id))
    team should not be empty
    team.get.memberIds should contain theSameElementsAs Seq(f.user1Id, f.user2Id)
  }

  it should "update existing team" in { f =>
    val team1Id = futureResultOrThrow(create(team1Clean)(tenant1.id))
    futureResultOrThrow(update(team1Id, team1Clean.copy(ambassadorId = Some(f.user1Id)))(tenant1.id))
    val team = futureResultOrThrow(teamWith(team1Id)(tenant1.id))
    team should not be empty
    team.get.ambassadorId should contain (f.user1Id)
  }

  it should "update members for team" in { f =>
    val team1Id = futureResultOrThrow(create(team1Clean)(tenant1.id))
    futureResultOrThrow(update(team1Id, team1Clean.copy(memberIds = Seq(f.user2Id)))(tenant1.id))
    val team = futureResultOrThrow(teamWith(team1Id)(tenant1.id))
    team should not be empty
    team.get.memberIds should contain only f.user2Id
  }

  it should "on update, not change id of existing team" in { f =>
    val team1Id = futureResultOrThrow(create(team1Clean)(tenant1.id))
    futureResultOrThrow(update(team1Id, team1Clean.copy(id = Some("ID123")))(tenant1.id))
    futureResultOrThrow(teamWith("ID123")(tenant1.id)) should be (empty)
    val team = futureResultOrThrow(teamWith(team1Id)(tenant1.id))
    team should not be empty
    team.get.id should contain (team1Id)
  }
}
