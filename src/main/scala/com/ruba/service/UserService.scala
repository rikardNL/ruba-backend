package com.ruba.service

import java.util.UUID

import com.ruba.UserNotFoundException
import com.ruba.api.{Tenant, User}
import com.ruba.repo.{UserRepository}
import com.twitter.util.Future

import scala.collection.mutable

/**
  * Created by rikard on 2016-02-07.
  */
trait UserService {
  def users(tenant: Tenant): Future[Seq[User]]
  def userWithId(tenant: Tenant)(id: String): Future[Option[User]]
  def create(tenant: Tenant)(user: User): Future[String]
  def update(tenant: Tenant)(id: String, user: User): Future[Unit]
  def delete(tenant: Tenant)(id: String): Future[Unit]
}

class IUserService(userRepository: UserRepository) extends UserService {

  override def users(tenant: Tenant): Future[Seq[User]] = {
    userRepository.usersFor(tenant.id)
  }

  override def userWithId(tenant: Tenant)(id: String): Future[Option[User]] = {
    userRepository.userWith(id)(tenant.id)
  }

  override def create(tenant: Tenant)(user: User): Future[String] = {
    userRepository.create(user)(tenant.id)
  }

  override def update(tenant: Tenant)(id: String, user: User): Future[Unit] = {
    userRepository.update(id, user)
  }

  override def delete(tenant: Tenant)(id: String): Future[Unit] = {
    Future.Done
  }
}

class InMemoryUserService(state: mutable.Map[Tenant, Map[String, User]] = mutable.Map.empty) extends UserService {

  override def users(tenant: Tenant): Future[Seq[User]] = {
    Future.value {
      state
        .get(tenant)
        .map(_.values.toSeq)
        .getOrElse(Seq.empty)
    }
  }

  override def userWithId(tenant: Tenant)(id: String): Future[Option[User]] = {
    Future.value(state.get(tenant).flatMap(_.get(id)))
  }

  override def create(tenant: Tenant)(user: User): Future[String] = {
    val newId = UUID.randomUUID().toString
    state += tenant -> state.getOrElse(tenant, Map.empty).updated(newId, user.copy(id = Some(newId)))
    Future.value(newId)
  }

  override def update(tenant: Tenant)(id: String, user: User): Future[Unit] = {
    state.get(tenant) match {
      case Some(userMap) if userMap.contains(id) =>
        state.update(tenant, userMap.updated(id, user.copy(id = Some(id))))
        Future.Done
      case _ =>
        Future.exception(UserNotFoundException(id))
    }
  }

  override def delete(tenant: Tenant)(id: String): Future[Unit] = {
    state += tenant -> (state.getOrElse(tenant, Map.empty) - id)
    Future.Done
  }
}
