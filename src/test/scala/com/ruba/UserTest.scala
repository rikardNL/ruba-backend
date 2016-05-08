package com.ruba

import org.scalatest.{Matchers, FlatSpec}

/**
  * Created by rikard on 2016-01-11.
  */
trait UserTest extends FlatSpec with Matchers {

  val tenants = Seq("tentant1", "tentant2")

  "User endpoint" should "return all users for company" in {
  }

  it should "not return users from other tenants" in {

  }

  it should "be able to return single users" in {

  }

  it should "be able to create users through posts" in {

  }

  it should "be able to patch existing users" in {

  }

  it should "return 404 if trying to patch a user that doesn't exist" in {

  }

  it should "return "

}

