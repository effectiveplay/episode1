package test.routing

import play.api.mvc._
import play.api.libs.json._
import play.api.test._
import play.api.test.Helpers._
import org.scalatest._
import org.scalatestplus.play._

import scala.concurrent.Future

import controllers._
import models._

class PostsRoutingSpec extends PlaySpec with OneAppPerSuite with Results {

  "Post routes" should {

    val post = Post(None, "Test Author", "Test Title", "Test Content")

    "route to #list" in {
      val Some(result) = route(FakeRequest(GET, "/posts"))

      status(result) mustEqual OK
      contentType(result) mustEqual Some("application/json")
      charset(result) mustEqual Some("utf-8")
      contentAsJson(result) mustEqual (Json.arr())
    }

    "route to #create" in {
      val Some(result) = route(FakeRequest(POST, "/posts").withBody(Json.toJson(post)))

      status(result) mustEqual OK
      contentType(result) mustEqual Some("application/json")
      charset(result) mustEqual Some("utf-8")
      contentAsJson(result) mustEqual (Json.toJson(post.copyWithId(1)))
    }

    "route to #get" in {
      val Some(result) = route(FakeRequest(GET, "/posts/1"))

      status(result) mustEqual OK
      contentType(result) mustEqual Some("application/json")
      charset(result) mustEqual Some("utf-8")
    }

    "route to #update" in {
      val updatedPost = post.copy(title = "Updated Title").copyWithId(1)
      val Some(result) = route(FakeRequest(GET, "/posts/1").withBody(Json.toJson(updatedPost)))

      status(result) mustEqual OK
    }

    "route to #delete" in {
      val Some(result) = route(FakeRequest(DELETE, "/posts/1"))

      status(result) mustEqual OK
    }

  }

}


