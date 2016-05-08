package com.ruba.service

import java.util.UUID

import com.ruba.TeamNotFoundException
import com.ruba.api.{Team, Tenant}
import com.ruba.repo.TeamRepository
import com.twitter.util.Future

import scala.collection.mutable

/**
  * Created by rikard on 2016-02-14.
  */
trait TeamService {
  def teams(tenant: Tenant): Future[Seq[Team]]
  def teamWithId(tenant: Tenant)(id: String): Future[Option[Team]]
  def create(tenant: Tenant)(team: Team): Future[String]
  def update(tenant: Tenant)(id: String, team: Team): Future[Unit]
  def delete(tenant: Tenant)(id: String): Future[Unit]
}

class ITeamService(teamRepository: TeamRepository) extends TeamService {
  override def teams(tenant: Tenant): Future[Seq[Team]] = {
    teamRepository.teamsFor(tenant.id)
  }

  override def teamWithId(tenant: Tenant)(id: String): Future[Option[Team]] = {
    teamRepository.teamWith(id)(tenant.id)
  }

  override def create(tenant: Tenant)(team: Team): Future[String] = {
    teamRepository.create(team)(tenant.id)
  }

  override def update(tenant: Tenant)(id: String, team: Team): Future[Unit] = {
    teamRepository.update(id, team)(tenant.id)
  }

  override def delete(tenant: Tenant)(id: String): Future[Unit] = {
    Future.Done
  }
}

class InMemoryTeamService(state: mutable.Map[Tenant, Map[String, Team]] = mutable.Map.empty) extends TeamService {

  override def teams(tenant: Tenant): Future[Seq[Team]] = {
    Future.value {
      state
        .get(tenant)
        .map(_.values.toSeq)
        .getOrElse(Seq.empty)
    }
  }

  override def teamWithId(tenant: Tenant)(id: String): Future[Option[Team]] = {
    Future.value(state.get(tenant).flatMap(_.get(id)))
  }

  override def create(tenant: Tenant)(team: Team): Future[String] = {
    val newId = UUID.randomUUID().toString
    state += tenant -> state.getOrElse(tenant, Map.empty).updated(newId, team.copy(id = Some(newId)))
    Future.value(newId)
  }

  override def update(tenant: Tenant)(id: String, team: Team): Future[Unit] = {
    state.get(tenant) match {
      case Some(teamsMap) if teamsMap.contains(id) =>
        state.update(tenant, teamsMap.updated(id, team.copy(id = Some(id))))
        Future.Done
      case _ =>
        Future.exception(TeamNotFoundException(id))
    }
  }

  override def delete(tenant: Tenant)(id: String): Future[Unit] = {
    state += tenant -> (state.getOrElse(tenant, Map.empty) - id)
    Future.Done
  }
}