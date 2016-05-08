package com.ruba.service

import com.ruba.{TestData, TenantNotFoundException, IdCollisionException, TestUtil}
import com.ruba.api.Tenant

/**
  * Created by rikard on 2016-02-21.
  */
class TenantServiceSpec extends ServiceTestComponent with TestData with TestUtil {

  class FixtureParam {
    val tenant = testTenants.head
    val tenants = testTenants
    val newTenant = Tenant(id = "NEWID", name = "NEWNAME")
    val tenantService = new InMemoryTenantService(mutableTenants(tenants))
  }

  override protected def withFixture(test: OneArgTest) = {
    super.withFixture(test.toNoArgTest(new FixtureParam))
  }

  "TenantService" should "list all tenants" in { f =>
    val res = futureResultOrThrow(f.tenantService.tenants)
    res.size should be (f.tenants.size)
    res should contain theSameElementsAs f.tenants
  }

  it should "get a tenant by id" in { f =>
    val res = futureResultOrThrow(f.tenantService.tenantWithId(f.tenant.id))
    res should not be empty
    res.get should be (f.tenant)
  }

  it should "return empty if tenant with id does not exist" in { f =>
    val res = futureResultOrThrow(f.tenantService.tenantWithId(f.newTenant.id))
    res should be (empty)
  }

  it should "create new tenant" in { f =>
    futureResultOrThrow {
      f.tenantService.create(f.newTenant)
    }
    val res = futureResultOrThrow {
      f.tenantService.tenantWithId(f.newTenant.id)
    }

    res should not be empty
    res.get should be (f.newTenant)
  }

  it should "refuse to create tenant with duplicate id" in { f =>
    intercept[IdCollisionException] {
      futureResultOrThrow {
        f.tenantService.create(f.tenant.copy(name = "OTHER NAME"))
      }
    }
  }

  it should "update a tenant" in { f =>
    futureResultOrThrow {
      f.tenantService.update(f.tenant.id, f.tenant.copy(name = "NEW NAME"))
    }

    val res = futureResultOrThrow {
      f.tenantService.tenantWithId(f.tenant.id)
    }
    res should not be empty
    res.get.name should be ("NEW NAME")
  }

  it should "not update id" in { f =>
    futureResultOrThrow {
      f.tenantService.update(f.tenant.id, Tenant(id = "NEWID", name = "NEW NAME"))
    }

    val res = futureResultOrThrow {
      f.tenantService.tenantWithId(f.tenant.id)
    }
    res should not be empty
    res.get.id should be (f.tenant.id)
    res.get.name should be ("NEW NAME")

    val res2 = futureResultOrThrow {
      f.tenantService.tenantWithId("NEWID")
    }
    res2 should be (empty)
  }

  it should "fail attempt to update non-existing tenant" in { f =>
    intercept[TenantNotFoundException] {
      futureResultOrThrow {
        f.tenantService.update("NONEXISTINGIID", f.tenant)
      }
    }

  }
}
