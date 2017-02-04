# Caliberweb Config

A simple DSL around the Typesafe config library.

I realize that this isn't exactly an original library, but I just started learning Cats and thought it was a good exercise.

## Usage

in build.sbt
```
resolvers += "caliberweb repo" at "https://s3-us-west-2.amazonaws.com/repo.caliberweb.com/release"
libraryDependencies += "com.caliberweb" %% "typesafe-config-dsl" % "1.1"
```

```scala
  import cats.implicits._
  import caliberweb.config._

  case class AllTheConfig(name: String, sub: SubConfig)
  case class SubConfig(number: Int, level3: Level3Config)
  case class Level3Config(enabled: Boolean, sections: Set[String])

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
```
