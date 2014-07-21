package utils

import play.api.libs.json.{JsValue, Reads}
import scala.concurrent.Future
import play.api.mvc.{Results, Result}
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits._

/**
 * Trait used to validate incoming Json
 */
trait JsonUtils extends Results {

  def validateJson[T](json: JsValue, success: (T, JsValue) =>
    Future[Result])(implicit reads: Reads[T]) = {
    json.validate[T].asEither match {
      case Left(errors) => {
        Logger.warn(s"Bad request : $errors")
        Future(BadRequest(errors.mkString(",")))
      }
      case Right(valid) => success(valid, json)
    }
  }

}