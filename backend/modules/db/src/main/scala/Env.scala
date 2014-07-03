package com.github.dbongo.db

import com.typesafe.config.Config
import play.modules.reactivemongo._
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.api._

final class Env() {
  lazy val db = {
    import play.api.Play.current
    ReactiveMongoPlugin.db
  }

  def collection(name: String) = db.collection[JSONCollection](name)

}

object Env {
  lazy val current = new Env()
}