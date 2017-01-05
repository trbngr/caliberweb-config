# Caliberweb Config

A simple DSL around the Typesafe config library.

# Here's an example of reading config into case classes.

```tut:silent
import cats.implicits._
import caliberweb.config._
import com.typesafe.config.ConfigFactory

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

 case class Level3Config(enabled: Boolean, sections: Set[String])
 case class SubConfig(number: Int, level3: Level3Config)
 case class AllTheConfig(name: String, sub: SubConfig)

 val dsl = getConfig("app") {
 val sub = getConfig("sub") {
   val nested = getConfig("nested") {
     (getBoolean("enabled") |@| getStringSet("sections")) map Level3Config
   }
   (getInt("number") |@| nested) map SubConfig
 }
 (getString("name") |@| sub) map AllTheConfig
 }

```

```tut
dsl.readFrom(configuration)
```