package models

import scala.slick.driver.JdbcProfile
import scala.slick.lifted.TableQuery
import play.api.db.slick.{ Config, DB, Profile }
import com.github.tototoshi.slick.GenericJodaSupport

trait DAOComponent {
  val profile: JdbcProfile

  final val jodaSupport = new GenericJodaSupport(Config.driver)

}

class DAO(val profile: JdbcProfile) extends
  DAOComponent with 
  CRUDComponent with
  PostComponent with
  Profile

object current {
  lazy val dao = new DAO(DB(play.api.Play.current).driver)
}

