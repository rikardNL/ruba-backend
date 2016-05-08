package com.ruba.repo.tables

import slick.driver.JdbcProfile

/**
  * Created by rikard on 2016-01-28.
  */
trait DbComponent {

  val driver: JdbcProfile

  import driver.api._

  val db: Database
}
