package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.db.slick._

import models._

abstract class CRUD[T <: CRUDModel[T] : Format](dao: DAO) { self: Controller =>

  val collection: CRUDComponent#CRUDTableQuery[T]

  def list(page: Int, pageSize: Int) = DBAction { implicit rs =>
    Ok(Json.toJson(collection.list(page, pageSize)))
  }

  def get(id: Long) = DBAction { implicit rs =>
    collection.get(id) match {
      case Some(item) => Ok(Json.toJson(item))
      case None       => NotFound
    }
  }

  def create = DBAction(parse.json) { implicit rs =>
    rs.request.body.validate[T] match {
      case JsSuccess(item, _) => Ok(Json.toJson(collection.create(item)))
      case JsError(errors)    => BadRequest
    }
  }

  def update(id: Long) = DBAction(parse.json) { implicit rs =>
    rs.request.body.validate[T] match {
      case JsSuccess(item, _) => collection.update(id, item.touch); Ok
      case JsError(errors)    => BadRequest
    }
  }

  def delete(id: Long) = DBAction { implicit rs =>
    val deleted = collection.delete(id)
    if (deleted) Ok else NotFound
  }

}


