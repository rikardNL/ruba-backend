package com.ruba.repo

import com.ruba.util.FutureConversion._
import com.twitter.util.Future
import slick.driver.H2Driver.api._

class IUserRepositoryTest
  extends DBTestComponent
    with IUserRepository with ITenantRepository with ITeamRepository {

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
    val team1Id = futureResultOrThrow(create(team1Clean)(tenant1.id))
    val team2Id = futureResultOrThrow(create(team2Clean)(tenant1.id))
    val team3Id = futureResultOrThrow(create(team3Clean)(tenant1.id))
    val team4Id = futureResultOrThrow(create(team4Clean)(tenant1.id))
  }

  before { futureResultOrThrow(db.run(allSchemas.create)) }

  after { futureResultOrThrow(db.run(allSchemas.drop)) }

  override protected def withFixture(test: OneArgTest) = {
    super.withFixture(test.toNoArgTest(new FixtureParam))
  }

  "UserRepository" should "get all users of a tenant" in { f =>
    futureResultOrThrow(create(user1Clean)(tenant1.id))
    futureResultOrThrow(create(user2Clean)(tenant1.id))
    futureResultOrThrow(create(user3Clean)(tenant1.id))
    futureResultOrThrow(create(user4Clean)(tenant2.id))

    val res = futureResultOrThrow(usersFor(tenant1.id)).map(_.email)
    res should contain allOf (user1Clean.email, user2Clean.email, user3Clean.email)
    res should not contain user4Clean.email
  }

  it should "get user by id" in { f =>
    val user1Id = futureResultOrThrow(create(user1Clean)(tenant1.id))
    futureResultOrThrow(create(user2Clean)(tenant1.id))

    val res = futureResultOrThrow(userWith(user1Id)(tenant1.id)).map(_.email)
    res should contain (user1Clean.email)
  }

  it should "not return user by id from wrong tenant" in { f =>
    val user1Id = futureResultOrThrow(create(user1Clean)(tenant1.id))
    val res = futureResultOrThrow(userWith(user1Id)(tenant2.id))
    res should be (empty)
  }

  it should "return empty when no user is found" in { f =>
    futureResultOrThrow(userWith("UNKNOWN_ID")(tenant1.id)) should be (empty)
  }

  it should "set a unique id for a new user" in { f =>
    val user1Id = futureResultOrThrow(create(user1Clean)(tenant1.id))
    val user2Id = futureResultOrThrow(create(user2Clean)(tenant1.id))
    user1Id should not be user2Id
  }

  it should "set images for a user" in { f =>
    val user1Id = futureResultOrThrow(create(user1Clean)(tenant1.id))
    val user = futureResultOrThrow(userWith(user1Id)(tenant1.id))
    user should not be empty
    user.get.imageUrls should not be empty
    user.get.imageUrls should contain theSameElementsAs user1Clean.imageUrls
  }

  it should "set teams for a user" in { f =>
    val user1Id = futureResultOrThrow(
      create(
        user1Clean.copy(teamIds = Seq(f.team1Id, f.team2Id))
      )(tenant1.id)
    )

    val user = futureResultOrThrow(userWith(user1Id)(tenant1.id))
    user should not be empty
    user.get.teamIds should contain theSameElementsAs Seq(f.team1Id, f.team2Id)
  }

  it should "update existing users" in { f =>
    val user1Id = futureResultOrThrow(create(user1Clean)(tenant1.id))
    futureResultOrThrow(update(user1Id, user1Clean.copy(email = "new@email.com")))
    val user = futureResultOrThrow(userWith(user1Id)(tenant1.id))
    user should not be empty
    user.get.email should be ("new@email.com")
  }

  it should "update images for user" in { f =>
    val image1 = "http://url.com"
    val image2 = "http://another.url.com"
    val user1Id = futureResultOrThrow(create(user1Clean.copy(imageUrls = Seq(image1)))(tenant1.id))
    futureResultOrThrow(update(user1Id, user1Clean.copy(imageUrls = Seq(image1, image2))))
    val user = futureResultOrThrow(userWith(user1Id)(tenant1.id))
    user should not be empty
    user.get.imageUrls should contain allOf(image1, image2)
  }

  it should "update teams for user" in { f =>
    val user1Id = futureResultOrThrow(create(user1Clean.copy(teamIds = Seq(f.team1Id)))(tenant1.id))
    futureResultOrThrow(update(user1Id, user1Clean.copy(teamIds = Seq(f.team1Id, f.team3Id))))
    val user = futureResultOrThrow(userWith(user1Id)(tenant1.id))
    user should not be empty
    user.get.teamIds should contain allOf(f.team1Id, f.team3Id)
  }

  it should "on update, not change id of existing user" in { f =>
    val user1Id = futureResultOrThrow(create(user1Clean)(tenant1.id))
    futureResultOrThrow(update(user1Id, user1Clean.copy(id = Some("ID123"))))
    futureResultOrThrow(userWith("ID123")(tenant1.id)) should be (empty)
    val user = futureResultOrThrow(userWith(user1Id)(tenant1.id))
    user should not be empty
    user.get.id should contain (user1Id)
  }
}
