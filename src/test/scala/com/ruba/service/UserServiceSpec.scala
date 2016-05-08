package com.ruba.service

import com.ruba.{TestData, UserNotFoundException, TestUtil}
import com.ruba.api.Tenant
import org.scalatest.Outcome

/**
  * Created by rikard on 2016-02-15.
  */
class UserServiceSpec extends ServiceTestComponent with TestData with TestUtil {

  class FixtureParam {
    val headTenant = testTenants.head
    val lastTenant = testTenants.last
    val headUsers = testUsers.take(2)
    val lastUsers = testUsers.slice(2, 3)
    val headTenantUsers = mutableUsers(headUsers, headTenant)
    val lastTenantUsers = mutableUsers(lastUsers, lastTenant)

    val newUser = testUsers.last

    val userService = new InMemoryUserService(headTenantUsers ++ lastTenantUsers)
  }

  override protected def withFixture(test: OneArgTest): Outcome = {
    super.withFixture(test.toNoArgTest(new FixtureParam))
  }

  "UserService" should "lists users for a tenant" in { f =>
    val res = futureResultOrThrow {
      f.userService.users(f.headTenant)
    }
    res.size should be (f.headUsers.size)
    res.map(_.email) should contain theSameElementsAs f.headUsers.map(_.email)
  }

  it should "get users for id" in { f =>
    val targetUser = f.headUsers.head

    val res = futureResultOrThrow {
      f.userService.userWithId(f.headTenant)(targetUser.id.get)
    }
    res should not be empty
    res.get should be (targetUser)
  }

  it should "return empty for no user found" in { f =>
    val res = futureResultOrThrow {
      f.userService.userWithId(f.headTenant)("UNKNOWN ID")
    }
    res should be (empty)
  }
  it should "return empty for unknown tenant" in { f =>
    val resList = futureResultOrThrow {
      f.userService.userWithId(Tenant(id = "UNKNOWN", name = "UNKNOWN"))(f.headUsers.head.id.get)
    }
    val resSingel = futureResultOrThrow {
      f.userService.users(Tenant(id = "UNKNOWN", name = "UNKNOWN"))
    }
    resList should be (empty)
    resSingel should be (empty)
  }

  it should "not get users from other tenants" in { f =>
    val res = futureResultOrThrow {
      f.userService.userWithId(f.lastTenant)(f.headUsers.head.id.get)
    }
    res should be (empty)
  }

  it should "create users for tenant" in { f =>
    val before = futureResultOrThrow {
      f.userService.userWithId(f.headTenant)(f.newUser.id.get)
    }
    before should be (empty)

    val id = futureResultOrThrow {
      f.userService.create(f.headTenant)(f.newUser)
    }
    id should not be empty
    id should not be f.newUser.id.get

    val res = futureResultOrThrow {
      f.userService.userWithId(f.headTenant)(id)
    }
    res should not be empty
    res.get.email should be (f.newUser.email)
  }

  it should "be able to delete a user" in  { f =>
    val deleteUser = f.headUsers.head

    val before = futureResultOrThrow {
      f.userService.userWithId(f.headTenant)(deleteUser.id.get)
    }
    before should not be empty
    futureResultOrThrow {
      f.userService.delete(f.headTenant)(deleteUser.id.get)
    }

    val res = futureResultOrThrow {
      f.userService.userWithId(f.headTenant)(deleteUser.id.get)
    }
    res should be (empty)

    val remaining = futureResultOrThrow {
      f.userService.users(f.headTenant)
    }
    remaining should not be empty
  }

  it should "handle deletion of non-existing user and non-existing tenant" in { f =>
    futureResultOrThrow {
      f.userService.delete(f.headTenant)(f.newUser.id.get)
    }
    futureResultOrThrow {
      f.userService.delete(Tenant(id = "UNKNOWN", name = "UNKNOWN"))(f.headUsers.head.id.get)
    }
    val res = futureResultOrThrow {
      f.userService.userWithId(f.headTenant)(f.headUsers.head.id.get)
    }
    res should not be empty
  }

  it should "be able to update an existing user" in { f =>
    val targetUser = f.headUsers.head
    val newName = "NewFirstName"

    futureResultOrThrow {
      f.userService.update(f.headTenant)(targetUser.id.get, targetUser.copy(firstName = Some(newName)))
    }
    val updated = futureResultOrThrow {
      f.userService.userWithId(f.headTenant)(targetUser.id.get)
    }

    updated should not be empty
    updated.get.firstName should be (Some(newName))
  }

  it should "not change id of updated user" in { f =>
    val targetUser = f.headUsers.head
    val newId = "NEWID"
    val newName = "NewFirstName"

    futureResultOrThrow {
      f.userService.update(f.headTenant)(
        id = targetUser.id.get,
        user = targetUser.copy(id = Some(newId), firstName = Some(newName))
      )
    }

    val res = futureResultOrThrow {
      f.userService.userWithId(f.headTenant)(targetUser.id.get)
    }
    res should not be empty
    res.get should be (targetUser.copy(firstName = Some(newName)))

    futureResultOrThrow {
      f.userService.userWithId(f.headTenant)(newId)
    } should be (empty)
  }

  it should "fail attempt to update non-existing user" in { f =>
    intercept[UserNotFoundException] {
      futureResultOrThrow {
        f.userService.update(f.headTenant)("NONEXISTINGID", f.headUsers.head)
      }
    }
  }
}