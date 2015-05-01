package test.integration

import play.api.mvc._
import play.api.libs.json._
import play.api.test._
import play.api.test.Helpers._
import org.scalatest._
import org.scalatestplus.play._

import play.api.db.slick.{ Config, DB, Profile }

import scala.concurrent.Future

import models._
import controllers._

class PostsControllerSpec extends PlaySpec with OneAppPerTest with Results {

  "Posts#list" must {

    "return empty array if no posts exist" in {
      val controller = new Posts(current.dao)
      val result = controller.list(0, 10).apply(FakeRequest())
      contentAsJson(result) must equal (Json.arr())
    }

    "return first page of posts" in {
      DB.withSession { implicit session =>
        val post = current.dao.posts.create(Post(None, "Ryan Tanner", "Test Post", "Test Content"))

        val controller = new Posts(current.dao)
        val result = controller.list(0, 10).apply(FakeRequest())
        contentAsJson(result) must equal (Json.arr(post))
      }
    }

    "paginate results" in {
      DB.withSession { implicit session =>

        val posts = for (_ <- 1 to 20) yield (current.dao.posts.create(Post(None, "Ryan Tanner", "Test Post", "Test Content")))

        val controller = new Posts(current.dao)
        val firstPage = controller.list(0, 10).apply(FakeRequest())
        contentAsJson(firstPage) must equal (Json.toJson(posts.take(10)))

        val secondPage = controller.list(1, 10).apply(FakeRequest())
        contentAsJson(secondPage) must equal (Json.toJson(posts.drop(10)))
      }
    }

  }

  "Posts#get" must {

    "return 404 if requested post does not exist" in {
      val controller = new Posts(current.dao)
      val result = controller.get(1l).apply(FakeRequest())
      status(result) must be (NOT_FOUND)
    }

    "return requested post if post exists for given ID" in {
      DB.withSession { implicit session =>
        val post = current.dao.posts.create(Post(None, "Ryan Tanner", "Test Post", "Test Content"))

        val controller = new Posts(current.dao)
        val result = controller.get(post.id.get).apply(FakeRequest())
        status(result) must be (OK)
        contentAsJson(result) must equal (Json.toJson(post))
      }
    }
    
  }

  "Posts#create" must {

    "return 400 if request includes invalid JSON" in {
      val controller = new Posts(current.dao)
      val result: Future[Result] = controller.create().apply(FakeRequest().withBody(Json.obj("foo" -> "bar")))
      status(result) must be (BAD_REQUEST)
    }

    "return 200 and created post" in {
      val post = Post(None, "Ryan Tanner", "Test Post", "Test Content")
      val controller = new Posts(current.dao)
      val result: Future[Result] = controller.create().apply(FakeRequest().withBody(Json.toJson(post)))
      status(result) must be (OK)
      val createdPost = contentAsJson(result).as[Post]

      createdPost.author must equal (post.author)
      createdPost.title must equal (post.title)
      createdPost.content must equal (post.content)
    }

  }

  "Posts#update" must {

    "return 400 if request includes invalid JSON" in {
      val controller = new Posts(current.dao)
      val result: Future[Result] = controller.update(1l).apply(FakeRequest().withBody(Json.obj("foo" -> "bar")))
      status(result) must be (BAD_REQUEST)
    }

    "throw NonPersistedItemException if post does not already exist" in {
      val post = Post(Some(1l), "Ryan Tanner", "Test Post", "Test Content")
      val controller = new Posts(current.dao)
      val result: Future[Result] = controller.update(1l).apply(FakeRequest().withBody(Json.toJson(post)))

      a [NonPersistedItemException] must be thrownBy status(result)
    }

    "return 200 if the post is successfully updated" in {
      DB.withSession { implicit session =>
        val post = current.dao.posts.create(Post(None, "Ryan Tanner", "Test Post", "Test Content"))

        val updatedPost = post.copy(title = "Updated Post")

        val controller = new Posts(current.dao)
        val result: Future[Result] = controller.update(post.id.get).apply(FakeRequest().withBody(Json.toJson(updatedPost)))
        status(result) must be (OK)

        val Some(currentPost) = current.dao.posts.get(1l)
        currentPost.title must equal (updatedPost.title)
        currentPost.updatedAt mustBe defined
      }
    }


  }

  "Posts#delete" must {

    "return 404 if post does not exist" in {
      val controller = new Posts(current.dao)
      val result: Future[Result] = controller.delete(1l).apply(FakeRequest())
      status(result) must be (NOT_FOUND)
    }

    "return 200 and delete post" in {
      DB.withSession { implicit session =>
        val post = current.dao.posts.create(Post(None, "Ryan Tanner", "Test Post", "Test Content"))

        val controller = new Posts(current.dao)
        val result: Future[Result] = controller.delete(post.id.get).apply(FakeRequest())

        status(result) must be (OK)

        current.dao.posts.get(post.id.get) mustBe empty
      }
    }    
  }

}


