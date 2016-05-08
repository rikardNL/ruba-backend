package com.ruba.repo

import java.util.UUID

import com.ruba.repo.tables.DbComponent
import com.ruba.{TestData, TestUtil}
import org.scalatest.{BeforeAndAfterAll, Matchers, BeforeAndAfter}
import org.scalatest.fixture

/**
  * Created by rikard on 2016-02-03.
  */
trait DBTestComponent extends fixture.FlatSpec
  with BeforeAndAfter
  with BeforeAndAfterAll
  with Matchers
  with DbComponent
  with TestData
  with TestUtil {

  override val driver = slick.driver.H2Driver

  import driver.api._

  val randomDb = "jdbc:h2:mem:test" + UUID.randomUUID().toString + ";"

  val h2Url = randomDb + "DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1"

  override val db: Database = Database.forURL(url = h2Url, driver = "org.h2.Driver")

  override def afterAll: Unit = {
    db.close()
  }
}
