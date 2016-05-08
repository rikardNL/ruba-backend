package com.ruba.api

import com.ruba.UserNotFoundException
import com.ruba.service.{TenantService, UserService}
import io.finch._
import io.finch.circe._
import io.circe.generic.auto._

/**
  * Created by rikard on 2016-02-04.
  */
trait UserEndpoint extends ApiHelpers {

  val tenantService: TenantService
  val userService: UserService

  val tenantUsers = string("tenant") / "users"

  val users: Endpoint[Seq[User]] = get(tenantUsers) { tenantId: String =>
    withTenant(tenantId) { tenant =>
      userService.users(tenant).map(Ok)
    }
  }

  val userWithId: Endpoint[User] = get(tenantUsers / string("userId")) { (tenantId: String, userId: String) =>
    withTenant(tenantId) { tenant =>
      userService.userWithId(tenant)(userId).map {
        case Some(user) => Ok(user)
        case None       => NotFound(UserNotFoundException(userId))
      }
    }
  }

  val createUser: Endpoint[String] = post(tenantUsers ? body.as[User]) { (tenantId: String, user: User) =>
    withTenant(tenantId) { tenant =>
      userService.create(tenant)(user).map(Created)
    }
  }

  val updateUser: Endpoint[Unit] = put(tenantUsers / string("userId") ? body.as[User]) {
    (tenantId: String, userId: String, user: User) =>
      withTenant(tenantId) { tenant =>
        userService.update(tenant)(userId, user).map(Ok)
      }
  }

  def endpoint = users :+: userWithId :+: createUser :+: updateUser
}

case class User(id: Option[String],
                email: String,
                firstName: Option[String],
                lastName: Option[String],
                phone: Option[String],
                streetAddress: Option[String],
                city: Option[String],
                zipCode: Option[String],
                socialSecurityNumber: Option[String],
                roleDescription: Option[String],
                presentation: Option[String],
                imageUrls: Seq[String],
                backgroundImage: Option[String],
                teamIds: Seq[String],
                ambassadorForTeams: Seq[String])

