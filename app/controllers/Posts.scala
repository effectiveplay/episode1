package controllers

import play.api._
import play.api.mvc._

import models._

class Posts(val dao: DAO) extends CRUD[Post](dao) with Controller {

  val collection = dao.posts

}
