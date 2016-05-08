package com.ruba.repo

import com.ruba.IdCollisionException
import com.ruba.util.FutureConversion._
import slick.driver.H2Driver.api._

class ITenantRepositoryTest
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
  }

  before { futureResultOrThrow(db.run(allSchemas.create)) }

  after { futureResultOrThrow(db.run(allSchemas.drop)) }

  override protected def withFixture(test: OneArgTest) = {
    super.withFixture(test.toNoArgTest(new FixtureParam))
  }

  "TenantRepository" should "get all tenants" in { f =>
    val res = futureResultOrThrow(tenants)
    res should not be empty
    res should contain allOf(tenant1, tenant2)
  }

  it should "get tenant by id" in { f =>
    val res = futureResultOrThrow(tenant(tenant1.id))
    res should contain (tenant1)
  }

  it should "return empty when no tenant is found" in { f =>
    futureResultOrThrow(tenant("UNKNOWN_ID")) should be (empty)
  }

  it should "update existing tenant" in { f =>
    futureResultOrThrow(update(tenant1.id, tenant1.copy(name = "NEW NAME")))
    val res = futureResultOrThrow(tenant(tenant1.id)).map(_.name)
    res should contain ("NEW NAME")
  }

  it should "on update, not change id of existing tenant" in { f =>
    futureResultOrThrow(update(tenant1.id, tenant1.copy(id = "newid")))
    futureResultOrThrow(tenant("newid")) should be (empty)
    val res = futureResultOrThrow(tenant(tenant1.id))
    res should contain (tenant1)
  }

  it should "fail if tenant id already exists" in { f =>
    intercept[IdCollisionException] {
      futureResultOrThrow(create(tenant1.copy(name = "Other name")))
    }
  }
}
