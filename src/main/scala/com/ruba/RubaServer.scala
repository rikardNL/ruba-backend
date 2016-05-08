package com.ruba

import com.ruba.api.{TenantEndpoint, TeamEndpoint, UserEndpoint}
import com.ruba.repo.tables.DbComponent
import com.ruba.repo.{ITenantRepository, ITeamRepository, IUserRepository}
import com.ruba.service._
import com.ruba.util.RubaConfig
import com.twitter.app.Flag
import com.twitter.finagle.Http
import com.twitter.server.TwitterServer
import com.twitter.util.Await
import com.typesafe.config.ConfigFactory

import io.finch.circe._
import io.circe.generic.auto._
import org.flywaydb.core.Flyway
import slick.driver.JdbcProfile

/**
  * Created by rian on 09/01/16.
  */
object RubaServer extends TwitterServer {

  val httpPort: Flag[Int] = flag("http.port", 9090, "Public http port for the Ruba service")

  val dbHost: Flag[String] = flag("db.host", "localhost", "Destination for database")
  val dbPort: Flag[Int] = flag("db.port", 5432, "Port for database")
  val dbName: Flag[String] = flag("db.name", "postgres", "Name of database")
  val dbUser: Flag[String] = flag("db.user", "postgres", "User for database")
  val dbPassword: Flag[String] = flag("db.password", "postgres", "Password for database")

  val config: RubaConfig = new RubaConfig(ConfigFactory.load("local").withFallback(ConfigFactory.load()))

  def main(): Unit = {
    val dbUrl = s"jdbc:postgresql://${dbHost()}:${dbPort()}/${dbName()}"

    log.info("Database settings:")
    log.info(s"  url = $dbUrl")
    log.info(s"  user = ${dbUser()}")
    log.info(s"  password = ${dbPassword()}")

    trait PostGresDb extends DbComponent {
      override val driver: JdbcProfile = slick.driver.PostgresDriver
      import driver.api.Database
      override val db: Database = Database.forURL(url = dbUrl, user = dbUser(), password = dbPassword())
    }

    val tenantRepo = new ITenantRepository with PostGresDb
    val userRepo = new IUserRepository with PostGresDb
    val teamRepo = new ITeamRepository with PostGresDb

    log.info("Applying migrations...")
    val flyway = new Flyway
    flyway.setDataSource(dbUrl, dbUser(), dbPassword())
    val migrations = flyway.migrate()
    log.info(s"Done migrating db. Applied $migrations migrations.")

    val iTenantService = new ITenantService(tenantRepo)
    val iUserService = new IUserService(userRepo)
    val iTeamService = new ITeamService(teamRepo)

    val tenantEndpoint = new TenantEndpoint {
      override val tenantService = iTenantService
    }.endpoint

    val userEndpoint = new UserEndpoint {
      override val userService: UserService = iUserService
      override val tenantService: TenantService = iTenantService
    }.endpoint

    val teamEndpoint = new TeamEndpoint {
      override val tenantService: TenantService = iTenantService
      override val teamService: TeamService = iTeamService
    }.endpoint

    val api = tenantEndpoint :+: userEndpoint :+: teamEndpoint

    val server = Http.server.serve(s":${httpPort()}", PingFilter() andThen api.toService)

    log.info(s"Serving Ruba Server on ${httpPort()}")

    onExit {
      server.close()
    }

    Await.ready(server)
  }
}