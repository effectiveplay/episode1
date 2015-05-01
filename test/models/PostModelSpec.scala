package com.effectiveplay.episode1.test.models

import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import play.api.db.slick.DB
import org.scalatest._
import org.scalatestplus.play._

import scala.concurrent.Future

import models._

class PostModelSpec extends PlaySpec with OneAppPerSuite {

  implicit override lazy val app: FakeApplication =
    FakeApplication(
      additionalConfiguration = inMemoryDatabase() 
    )

  "Posts" must {

    val post = Post(None, "Test Title", "Test Author", "Test Content")

    "be created" in {
      DB.withSession { implicit session =>
        val createdPost = current.dao.posts.create(post)
        createdPost.id mustEqual (Some(1l))
      }
    }

    "be retrieved by ID" in {
      DB.withSession { implicit session =>
        current.dao.posts.get(1l) mustBe defined
      }
    }

    "be listed" in {
      DB.withSession { implicit session =>
        val list = current.dao.posts.list() 
        list must have size (1)
        list.head.id mustEqual (Some(1l))
      }
    }

    "be updated" in {
      DB.withSession { implicit session =>
        current.dao.posts.update(1l, post.copy(title = "Updated Title").copyWithId(1l))

        val Some(updatedPost) = current.dao.posts.get(1l)

        updatedPost.title mustEqual "Updated Title"
      }
    }

    "be deleted" in {
      DB.withSession { implicit session =>
        current.dao.posts.delete(1l)

        current.dao.posts.get(1l) mustBe empty
      }
    }

  }

}



