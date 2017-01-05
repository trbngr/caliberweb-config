package caliberweb.config

import cats.implicits._
import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}

class Specs extends FlatSpec with Matchers {

  case class AllTheConfig(name: String, sub: SubConfig)
  case class SubConfig(number: Int, level3: Level3Config)
  case class Level3Config(enabled: Boolean, sections: Set[String])
  
  val configuration = ConfigFactory.parseString(
    s"""
       | app {
       |   name: test
       |   sub {
       |     number: 42
       |     nested {
       |       enabled: false
       |       sections: ["one", "two", "two", "three"]
       |     }
       |   }
       | }
     """.stripMargin)

  "AllTheConfig" should "be read" in {

    val dsl = getConfig("app") {
      val sub = getConfig("sub") {
        val nested = getConfig("nested") {
          (getBoolean("enabled") |@| getStringSet("sections")) map Level3Config
        }
        (getInt("number") |@| nested) map SubConfig
      }
      (getString("name") |@| sub) map AllTheConfig
    }

    val config = dsl.readFrom(configuration)
    
    config.name shouldBe "test"
    config.sub.number shouldBe 42
    config.sub.level3.enabled shouldBe false
    config.sub.level3.sections should contain only("one", "two", "three")
    
  }

}
