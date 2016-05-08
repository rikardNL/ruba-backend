package com.ruba.repo.tables

/**
  * Created by rikard on 2016-01-16.
  */
case class DbUser(id: String,
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
                  backgroundImage: Option[String])

private[repo] trait UsersTable extends TenantsTable { self: DbComponent =>
  import driver.api._
  class Users(tag: Tag) extends Table[DbUser](tag, "Users") {
    def id = column[String]("id", O.PrimaryKey)
    def email = column[String]("email")
    def firstName = column[Option[String]]("first_name")
    def lastName = column[Option[String]]("last_name")
    def phone = column[Option[String]]("phone")
    def streetAddress = column[Option[String]]("street_address")
    def city = column[Option[String]]("city")
    def zipCode = column[Option[String]]("zip_code")
    def socialSecurityNumber = column[Option[String]]("ssn")
    def roleDescription = column[Option[String]]("role_description")
    def presentation = column[Option[String]]("presentation")
    def backgroundImage = column[Option[String]]("background_image")

    override def * = (id, email, firstName, lastName, phone, streetAddress, city, zipCode,
      socialSecurityNumber, roleDescription, presentation, backgroundImage) <> (DbUser.tupled, DbUser.unapply)
  }

  protected val usersTable = TableQuery[Users]

  protected def userWithIdQ(id: String) = {
    usersTable.filter(_.id === id)
  }

  protected def updateUserQ(id: String, user: DbUser) = {
    usersTable.insertOrUpdate(user.copy(id = id))
  }

  protected def createUserQ(user: DbUser) = {
    usersTable += user
  }
}
