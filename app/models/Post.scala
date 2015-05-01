package models

import play.api.libs.json.Json
import scala.slick.lifted.Tag
import org.joda.time.DateTime

case class Post(
  id: Option[Long],
  author: String,
  title: String,
  content: String,
  updatedAt: Option[DateTime] = None,
  createdAt: DateTime = DateTime.now) extends CRUDModel[Post] {

  def copyWithId(id: Long) = copy(id = Some(id))

  def touch = copy(updatedAt = Some(DateTime.now))

}

object Post {

  implicit val fmt = Json.format[Post]

}

trait PostComponent { self: CRUDComponent with DAOComponent =>

  import profile.simple._
  import jodaSupport._

  class PostsTable(tag: Tag) extends CRUDTable[Post](tag, "post") {
    def author        = column[String]("author", O.NotNull)
    def title         = column[String]("title", O.NotNull)
    def content       = column[String]("content", O.NotNull)
    def updatedAt     = column[Option[DateTime]]("updated_at")
    def createdAt     = column[DateTime]("created_at", O.NotNull)

    def * = (id.?, author, title, content,
      updatedAt, createdAt) <> ((Post.apply _).tupled, Post.unapply _)

  }

  implicit final val posts = new CRUDTableQuery[Post](new PostsTable(_))

}
