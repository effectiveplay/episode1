package models

import play.api.libs.json.Json
import play.api.db.slick.Session

import scala.slick.lifted._
import scala.slick.driver.JdbcProfile

import org.joda.time.{ DateTime, Period }

trait CRUDModel[E <: CRUDModel[E]] {

  def id: Option[Long]

  def copyWithId(id: Long): E

  def touch: E

}

trait CRUDComponent { self: DAOComponent =>

  import profile.simple._

  abstract class CRUDTable[E <: CRUDModel[E]](tag: Tag, tableName: String) extends Table[E](tag, tableName) {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  }

  class CRUDTableQuery[E <: CRUDModel[E]](cons: Tag => CRUDTable[E]) extends TableQuery(cons) { 

    def list(page: Int = 0, pageSize: Int = 10)(implicit session: Session): List[E] = {
      this.drop(pageSize * page).take(pageSize).list
    }

    def get(id: Long)(implicit session: Session): Option[E] = {
      this.filter(_.id === id).firstOption
    }

    def create(item: E)(implicit session: Session): E = {
      val id = (this returning this.map(_.id)) insert item
      item.copyWithId(id)
    }

    def delete(id: Long)(implicit session: Session): Boolean = {
      val deletedRows = this.filter(_.id === id).delete

      deletedRows > 0
    }

    def update(id: Long, item: E)(implicit session: Session): Unit = {
      if (this.filter(_.id === id).update(item) != 1)
        throw NonPersistedItemException()
    }

  }

  object CRUDTableQuery {

    def apply[E <: CRUDModel[E] : CRUDTableQuery]: CRUDTableQuery[E] = implicitly[CRUDTableQuery[E]]

  }

}

case class NonPersistedItemException() extends Exception("Cannot update item that has not been persisted")

