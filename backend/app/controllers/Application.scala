package controllers

import utils._
import play.api._
import play.api.mvc._
import scala.concurrent.duration._
import scala.concurrent.Future
import play.api.libs.concurrent._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.bson.DefaultBSONHandlers._
import play.modules.reactivemongo._
import play.modules.reactivemongo.json.BSONFormats._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Play.current
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONDocument
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import reactivemongo.bson.Producer.nameValue2Producer

object Application extends Controller with MongoController {

  def collection: JSONCollection = db.collection[JSONCollection]("users")

  def index = Action { Ok("pong") }

  /** Full User validator */
  val validateUser: Reads[JsObject] = (
    (__ \ 'uid).json.pickBranch and
      (__ \ 'eventId).json.pickBranch) reduce

  /** User validator for restricted update */
  val emptyObj = __.json.put(Json.obj())
  val validateUser4RestrictedUpdate: Reads[JsObject] = (
    ((__ \ 'uid).json.pickBranch or emptyObj) and
      ((__ \ 'eventId).json.pickBranch or emptyObj)).reduce

  /** Writes an String ID in Json Extended Notation */
  val toId = OWrites[String] { s => Json.obj("auth_token" -> s) }

  /** Updates Json by adding metadata fields */
  val generateId = (__ \ 'auth_token).json.put(JsString(BSONObjectID.generate.stringify))
  val addMetadata: Reads[JsObject] = __.json.update(generateId)

  /** don't output the technical mongodb id */
  val outputUser = (__ \ '_uid).json.prune
  val outputId = (__ \ 'auth_token).json.pick

  /** Converts JSON into Mongo update selector by just copying whole object in $set field */
  val toMongoUpdate = (__ \ '$set).json.copyFrom(__.json.pick)

  def findAll = Action.async {
    val cursor = collection.find(BSONDocument(), BSONDocument()).cursor[JsValue]
    val futureList = cursor.collect[List]()
    futureList
      .map { results => results.map { result => result.transform(outputUser).get } }
      .map { results => Ok(Json.toJson(results)) }
  }

  def create = Action.async(parse.json) { request =>
    request.body.transform(validateUser andThen addMetadata).map {
      jsobj =>
        collection.insert(jsobj)
          .map { lastError => Created(Json.obj("auth_token" -> jsobj.transform(outputId).get, "msg" -> "User Created")) }
          .recover { case e => InternalServerError(JsString("exception %s".format(e.getMessage))) }
    }.recoverTotal {
      err =>
        Future.successful(BadRequest(JsError.toFlatJson(err)))
    }
  }

  def find(uid: String) = Action.async {
    collection.find(BSONDocument("uid" -> uid)).cursor[JsValue].headOption.map {
      case None => NotFound(Json.obj("msg" -> s"User with ID $uid not found"))
      case Some(p) =>
        p.transform(outputUser)
          .map { user => Ok(user) }
          .recoverTotal { e => BadRequest(JsError.toFlatJson(e)) }
    }
  }

  def delete(uid: String) = Action.async {
    collection.remove[JsValue](toId.writes(uid)).map { lastError =>
      if (lastError.ok)
        Ok(Json.obj("msg" -> s"User Deleted"))
      else
        InternalServerError(JsString("error %s".format(lastError.stringify)))
    }
  }

  def update(uid: String) = Action.async(parse.json) { request =>
    request.body.transform(validateUser4RestrictedUpdate).flatMap { jsobj =>
      jsobj.transform(toMongoUpdate).map { updateSelector =>
        collection.update(toId.writes(uid), updateSelector).map { lastError =>
          if (lastError.ok)
            Ok(Json.obj("msg" -> s"User Updated"))
          else
            InternalServerError(JsString("error %s".format(lastError.stringify)))
        }
      }
    }.recoverTotal { e =>
      Future.successful(BadRequest(JsError.toFlatJson(e)))
    }
  }
}
