package caliberweb

import com.typesafe.config.Config

import cats.free.FreeApplicative
import cats.free.FreeApplicative.lift
import cats.~>
import scala.collection.JavaConverters._

package object config {

  sealed abstract class ConfigOp[A] extends Product with Serializable

  final case class GetInt(field: String) extends ConfigOp[Int]
  final case class GetBoolean(field: String) extends ConfigOp[Boolean]
  final case class GetString(field: String) extends ConfigOp[String]
  final case class GetStrings(field: String) extends ConfigOp[List[String]]
  final case class GetConfig[A](field: String, dsl: ConfigDsl[A]) extends ConfigOp[A]

  type ConfigDsl[A] = FreeApplicative[ConfigOp, A]

  def int(field: String): ConfigDsl[Int] = lift(GetInt(field))
  def bool(field: String): ConfigDsl[Boolean] = lift(GetBoolean(field))
  def string(field: String): ConfigDsl[String] = lift(GetString(field))
  def stringList(field: String): ConfigDsl[List[String]] = lift(GetStrings(field))
  def stringSet(field: String): ConfigDsl[Set[String]] = stringList(field).map(_.toSet)
  def inSection[A](field: String)(dsl: ConfigDsl[A]): ConfigDsl[A] = lift(GetConfig(field, dsl))

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
    def from(config: Config): A = dsl.foldMap(interpret).apply(config)
  }

}
