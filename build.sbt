import com.amazonaws.services.s3.model.Region

organization := "com.caliberweb"
name := "typesafe-config-dsl"
version := "1.2"

scalaVersion := "2.12.1"
crossScalaVersions := Seq("2.11.8", "2.12.1")
scalacOptions := Seq(
  "-encoding", "UTF-8",
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Ywarn-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-unused",
  "-Xlint:-infer-any",
  "-Xfatal-warnings",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-language:higherKinds"
)

libraryDependencies += "com.typesafe" % "config" % "1.3.1"
libraryDependencies += "org.typelevel" %% "cats-free" % "0.9.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % Test

s3region := Region.US_West_2
s3overwrite := true
publishArtifact in Test := false
pomIncludeRepository := (_ ⇒ true)
publishMavenStyle := true
publishTo := {
  val folder = if (isSnapshot.value) "snapshot" else "release"
  Some(s3resolver.value("caliberweb repo", s3(s"repo.caliberweb.com/$folder")) withMavenPatterns)
}