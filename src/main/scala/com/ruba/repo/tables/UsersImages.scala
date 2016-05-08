package com.ruba.repo.tables

/**
  * Created by rikard on 2016-02-25.
  */
case class DbUserImage(user: String,
                       url: String)

private[repo] trait UserImagesTable extends UsersTable { self: DbComponent =>
  import driver.api._

  class UserImages(tag: Tag) extends Table[DbUserImage](tag, "UserImages") {
    def user = column[String]("user")
    def url = column[String]("url")

    def pk = primaryKey("pk_users_images", (user, url))
    def userFk = foreignKey("ui_user_fk", user, usersTable)(_.id)

    override def * = (user, url) <> (DbUserImage.tupled, DbUserImage.unapply)
  }

  protected val userImagesTable = TableQuery[UserImages]

  protected def imagesForUserQ(userId: Rep[String]) = {
    userImagesTable.filter(_.user === userId)
  }

  protected def addImageForUserQ(userId: String, url: String) = {
    userImagesTable += DbUserImage(userId, url)
  }

  protected def addImagesForUserQ(userId: String, urls: Seq[String]) = {
    val images = urls.map(DbUserImage(userId, _))
    userImagesTable ++= images
  }

  protected def deleteImageForUserQ(userId: String, url: String) = {
    userImagesTable
      .filter(_.user === userId)
      .filter(_.url === url)
      .delete
  }

  protected def deleteImagesForUserQ(userId: String, urls: Seq[String]) = {
    userImagesTable
      .filter(_.user === userId)
      .filter(_.url inSet urls)
      .delete
  }

  private def deleteImagesForUserQ(userId: String) = {
    imagesForUserQ(userId).delete
  }

  protected def setImagesForUserQ(userId: String, urls: Seq[String]) = {
    deleteImagesForUserQ(userId) >> addImagesForUserQ(userId, urls)
  }
}
