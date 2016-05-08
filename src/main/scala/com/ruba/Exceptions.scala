package com.ruba

/**
  * Created by rikard on 2016-02-07.
  */
case class TenantNotFoundException(tenantId: String) extends Exception(s"tenant with id $tenantId not found")
case class UserNotFoundException(userId: String) extends Exception(s"user with id $userId not found")
case class TeamNotFoundException(teamId: String) extends Exception(s"team with id $teamId not found")

case class IdCollisionException(val message: String) extends Exception(message)
