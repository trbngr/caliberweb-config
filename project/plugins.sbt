logLevel := Level.Warn
resolvers += "Era7 maven releases" at "https://s3-eu-west-1.amazonaws.com/releases.era7.com"
addSbtPlugin("ohnosequences" % "sbt-s3-resolver" % "0.15.0")

addSbtPlugin("org.tpolecat" % "tut-plugin" % "0.4.8")
addSbtPlugin("com.fortysevendeg"  % "sbt-microsites" % "0.4.0")
