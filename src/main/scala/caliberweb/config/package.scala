package caliberweb

import com.typesafe.config.Config

import cats.free.FreeApplicative
import cats.free.FreeApplicative.lift
import cats.~>
import scala.collection.JavaConverters._

package object config {

  sealed trait ConfigOp[A]

  case class GetInt(field: String) extends ConfigOp[Int]
  case class GetBoolean(field: String) extends ConfigOp[Boolean]
  case class GetString(field: String) extends ConfigOp[String]
  case class GetStrings(field: String) extends ConfigOp[List[String]]
  case class GetConfig[A](field: String, dsl: ConfigDsl[A]) extends ConfigOp[A]

  type ConfigDsl[A] = FreeApplicative[ConfigOp, A]

  def getInt(field: String): ConfigDsl[Int] = lift(GetInt(field))
  def getBoolean(field: String): ConfigDsl[Boolean] = lift(GetBoolean(field))
  def getString(field: String): ConfigDsl[String] = lift(GetString(field))
  def getStringList(field: String): ConfigDsl[List[String]] = lift(GetStrings(field))
  def getStringSet(field: String): ConfigDsl[Set[String]] = getStringList(field).map(_.toSet)
  def getConfig[A](field: String)(dsl: ConfigDsl[A]): ConfigDsl[A] = lift(GetConfig(field, dsl))

  type FromConfig[A] = Config ⇒ A

  def interpret(implicit G: cats.Applicative[FromConfig]): ConfigOp ~> FromConfig = new (ConfigOp ~> FromConfig) {
    def apply[A](op: ConfigOp[A]) = config ⇒ op match {
      case GetConfig(field, value) ⇒ value.foldMap(interpret).apply(config.getConfig(field))
      case GetBoolean(field)       ⇒ config.getBoolean(field)
      case GetInt(field)           ⇒ config.getInt(field)
      case GetString(field)        ⇒ config.getString(field)
      case GetStrings(field)       ⇒ config.getStringList(field).asScala.toList
    }
  }

  implicit class ConfigReader[A](dsl: ConfigDsl[A])(implicit G: cats.Applicative[FromConfig]){
    def readFrom(config: Config): A = dsl.foldMap(interpret).apply(config)
  }

}
