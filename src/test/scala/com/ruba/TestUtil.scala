package com.ruba

import com.ruba.api.{User, Team, Tenant}
import com.twitter.util.{Await, Awaitable}

import scala.collection.mutable

/**
  * Created by rikard on 2016-01-28.
  */
trait TestUtil {

  def mutableTenants(tenants: Seq[Tenant]): mutable.Map[String, Tenant] = {
    val mutTenants = mutable.Map.empty[String, Tenant]
    tenants foreach { tenant =>
      mutTenants.update(tenant.id, tenant)
    }
    mutTenants
  }

  def mutableTeams(teams: Seq[Team], tenant: Tenant): mutable.Map[Tenant, Map[String, Team]] = {
    val teamsMap = teams.flatMap({ team => team.id.map(_ -> team) }).toMap
    mutable.Map(tenant -> teamsMap)
  }

  def mutableUsers(users: Seq[User], tenant: Tenant): mutable.Map[Tenant, Map[String, User]] = {
    val usersMap = users.flatMap({ user => user.id.map(_ -> user) }).toMap
    mutable.Map(tenant -> usersMap)
  }

  def futureResultOrThrow[T](f: => Awaitable[T]): T = {
    Await.result(f)
  }
}
