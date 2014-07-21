package models
/**
 *
 * @author  Michael Crowther
 *          created 7/20/14
 */
import play.api.libs.json.Json

case class Login(uid: String, eventId: String) {}

object Login {
  implicit val loginFormat = Json.format[Login]
}
