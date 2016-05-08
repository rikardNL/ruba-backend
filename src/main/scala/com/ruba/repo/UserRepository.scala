package com.ruba.repo

import java.util.UUID

import com.ruba.api.User
import com.ruba.repo.tables._
import com.ruba.util.FutureConversion.ScalaFutureExtention
import com.twitter.util.Future

/**
  * Created by rikard on 2016-02-25.
  */
trait UserRepository {
  def usersFor(tenantId: String): Future[Seq[User]]
  def userWith(id: String)(tenantId: String): Future[Option[User]]
  def create(user: User)(tenantId: String): Future[String]
  def update(userId: String, user: User): Future[Unit]
}

trait IUserRepository
  extends UserRepository
    with UsersTable
    with UsersTeamsTable
    with UserImagesTable
    with UsersTenantsTable
    with TeamsTable { self: DbComponent =>

  import driver.api._
  import UserConversions._
  import scala.concurrent.ExecutionContext.Implicits.global

  override def usersFor(tenantId: String): Future[Seq[User]] = {
    db.run[UsersQueryResponse] {
      val usersResults = usersForTenantQ(tenantId).result
      for {
        users <- usersResults
        teams <- teamsFor(users)
        images <- imagesFor(users)
      } yield (users, teams, images)
    }.map(mergeUsersResponse).toTwitterFuture
  }

  override def userWith(id: String)(tenantId: String): Future[Option[User]] = {
    db.run[UsersQueryResponse] {
      val userResult = usersForTenantQ(tenantId).filter(_.id === id).result
      for {
        user <- userResult
        teams <- teamsFor(user)
        images <- imagesFor(user)
      } yield (user, teams, images)
    }.map(mergeUsersResponse).map(_.headOption).toTwitterFuture
  }

  override def create(user: User)(tenantId: String): Future[String] = {
    db.run[String] {
      val dbUser = toDbUserWithId(user)
      createUserQ(dbUser) >>
        addUserForTenantQ(dbUser.id, tenantId) >>
        setImagesForUserQ(dbUser.id, user.imageUrls) >>
        setTeamsForUserQ(dbUser.id, user.teamIds)
      .map(_ => dbUser.id)
    }.toTwitterFuture
  }

  override def update(userId: String, user: User): Future[Unit] = {
    db.run {
      updateUserQ(userId, toDbUser(user)) >>
        setImagesForUserQ(userId, user.imageUrls) >>
          setTeamsForUserQ(userId, user.teamIds)
    }.toTwitterFuture.unit
  }

  private def teamsFor(users: Seq[DbUser]) = {
    DBIO.sequence(
      users.map { user =>
        teamsForUserQ(user.id).result.map((user, _))
      }
    )
  }

  private def imagesFor(users: Seq[DbUser]) = {
    DBIO.sequence(
      users.map { user =>
        imagesForUserQ(user.id).result.map((user, _))
      }
    )
  }

  type UsersQueryResponse = (Seq[DbUser], Seq[(DbUser, Seq[DbTeam])], Seq[(DbUser, Seq[DbUserImage])])

  private def mergeUsersResponse(response: UsersQueryResponse): Seq[User] = {
    response match {
      case (users, usersTeams, usersImages) =>
        val teams = usersTeams.toMap.withDefaultValue(Seq.empty)
        val images = usersImages.toMap.withDefaultValue(Seq.empty)
        users.map { user =>
          toUser(user, teams(user), images(user))
        }
    }
  }
}

object UserConversions {

  def toUser(dbUser: DbUser, dbTeams: Seq[DbTeam], images: Seq[DbUserImage]): User = {
    val ambassadorFor = dbTeams.filter(_.ambassador.exists(_ == dbUser.id)).map(_.id)

    User(
      id = Some(dbUser.id),
      email = dbUser.email,
      firstName = dbUser.firstName,
      lastName = dbUser.lastName,
      phone = dbUser.phone,
      streetAddress = dbUser.streetAddress,
      city = dbUser.city,
      zipCode = dbUser.zipCode,
      socialSecurityNumber = dbUser.socialSecurityNumber,
      roleDescription = dbUser.roleDescription,
      presentation = dbUser.presentation,
      imageUrls = images.map(_.url),
      backgroundImage = dbUser.backgroundImage,
      teamIds = dbTeams.map(_.id),
      ambassadorForTeams = ambassadorFor
    )
  }

  def toDbUser(user: User): DbUser = {
    user.id match {
      case None => toDbUserWithId(user)
      case Some(id) =>
        DbUser(
          id = id,
          email = user.email,
          firstName = user.firstName,
          lastName = user.lastName,
          phone = user.phone,
          streetAddress = user.streetAddress,
          city = user.city,
          zipCode = user.zipCode,
          socialSecurityNumber = user.socialSecurityNumber,
          roleDescription = user.roleDescription,
          presentation = user.presentation,
          backgroundImage = user.backgroundImage
        )
    }
  }

  def toDbUserWithId(user: User): DbUser = {
    toDbUser(
      user = user.copy(id = Some(UUID.randomUUID().toString))
    )
  }
}
