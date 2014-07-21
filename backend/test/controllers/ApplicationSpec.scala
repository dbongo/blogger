package controllers

import play.api.libs.ws.{WSResponse, WS}

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import models._
import utils._
import scala.concurrent.{ExecutionContext, Future}
import play.Logger
import ExecutionContext.Implicits.global
import reactivemongo.bson.BSONObjectID

/**
 *
 *  GET      /api/ping                      controllers.Application.index
 *  GET      /api/users                     controllers.Application.findAll
 *  POST     /api/users                     controllers.Application.create
 *  GET      /api/users/:id                 controllers.Application.find(id: String)
 *  PUT      /api/users/:id                 controllers.Application.update(id: String)
 *  DELETE   /api/users/:id                 controllers.Application.delete(id: String)
 *
 * @author  Michael Crowther
 *          created 7/20/14
 */
class ApplicationSpec extends PlaySpecification with EmbeddedMongo {

  val pingUrl = "http://localhost:19001/api/ping"
  val listUsersUrl = "http://localhost:19001/api/users"
  val createUserUrl = "http://localhost:19001/api/users"
  val userUrl = "http://localhost:19001/api/users/"

  def uid(): String = TokenGenerator.generate

  "api should create and read a set of users" in new WithServer(new FakeApplication(additionalConfiguration = inMemoryMongoDatabase())) {
    val id1 = uid()
    val id2 = uid()

    val user = Login(id1, "event1")
    Logger.warn(user.toString)

    val user2 = Login(id2, "event1")
    Logger.warn(user2.toString)

    val pingResponse: WSResponse = await(WS.url(pingUrl).get())
    Logger.warn(pingResponse.body)
    pingResponse.body must beEqualTo("pong")

    val response: WSResponse = await(WS.url(createUserUrl).post(Json.toJson(user)))
    Logger.warn(response.toString)
    response.status must beEqualTo(CREATED)

    val response2: WSResponse = await(WS.url(createUserUrl).post(Json.toJson(user2)))
    Logger.warn(response2.toString)
    response2.status must beEqualTo(CREATED)

    val listUsers: WSResponse = await(WS.url(listUsersUrl).get())
    Logger.warn(Json.stringify(listUsers.json))
    listUsers.status must beEqualTo(OK)

    val foundUser: WSResponse = await(WS.url(userUrl + id1).get())
    foundUser.status must beEqualTo(OK)
    Logger.warn(Json.stringify(foundUser.json))

    val foundUser2: WSResponse = await(WS.url(userUrl + id2).get())
    foundUser2.status must beEqualTo(OK)
    Logger.warn(Json.stringify(foundUser2.json))
  }
}
