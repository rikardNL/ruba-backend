package com.ruba.service

import com.ruba.repo.TenantRepository
import com.ruba.{TenantNotFoundException, IdCollisionException}
import com.ruba.api.Tenant
import com.twitter.util.Future

import scala.collection.mutable

/**
  * Created by rikard on 2016-02-14.
  */
trait TenantService {
  def tenants: Future[Seq[Tenant]]
  def tenantWithId(id: String): Future[Option[Tenant]]
  def create(tenant: Tenant): Future[Unit]
  def update(id: String, tenant: Tenant): Future[Unit]
  def delete(id: String): Future[Unit]
}

class ITenantService(tenantRepository: TenantRepository) extends TenantService {

  override def tenants: Future[Seq[Tenant]] = {
    tenantRepository.tenants
  }

  override def tenantWithId(id: String): Future[Option[Tenant]] = {
    tenantRepository.tenant(id)
  }

  override def create(tenant: Tenant): Future[Unit] = {
    tenantRepository.create(tenant)
  }

  override def update(id: String, tenant: Tenant): Future[Unit] = {
    tenantRepository.update(id, tenant)
  }

  override def delete(id: String): Future[Unit] = {
    Future.Done
  }
}

class InMemoryTenantService(state: mutable.Map[String, Tenant] = mutable.Map.empty) extends TenantService {

  override def tenants: Future[Seq[Tenant]] = {
    Future.value(state.values.toSeq)
  }

  override def update(id: String, tenant: Tenant): Future[Unit] = {
    state.get(id) match {
      case Some(_)  => Future.value(state.update(id, tenant.copy(id = id)))
      case None     => Future.exception(TenantNotFoundException(id))
    }
  }

  override def tenantWithId(id: String): Future[Option[Tenant]] = {
    Future.value(state.get(id))
  }

  override def delete(id: String): Future[Unit] = {
    state.remove(id) match {
      case Some(_)  => Future.Done
      case None     => Future.exception(TenantNotFoundException(id))
    }
  }

  override def create(tenant: Tenant): Future[Unit] = {
    state.contains(tenant.id) match {
      case true   => Future.exception(IdCollisionException("Tenant Id already exists"))
      case false  =>
        state += tenant.id -> tenant
        Future.Done
    }
  }
}