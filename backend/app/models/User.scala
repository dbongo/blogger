package models
/**
 *
 * @author  Michael Crowther
 *          created 7/20/14
 */
case class User(
  uid: String,
  eventId: String,
  firstName: String,
  lastName: String,
  email: String)

object User {
  import play.api.libs.json.Json
  import play.api.data._
  import play.api.data.Forms._

  implicit val userFormat = Json.format[User]

  val userForm = Form(
    mapping(
      "uid" -> nonEmptyText,
      "eventId" -> nonEmptyText,
      "firstName" -> text,
      "lastName" -> text,
      "email" -> nonEmptyText)(User.apply _)(User.unapply _))

}
