organization := "com.github.petekneller"

name := "experiments-scala-type-system"

version := "dev"

scalaVersion := "2.11.5"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.2" % "test"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.0"

libraryDependencies += "com.chuusai" %% "shapeless" % "2.1.0"

libraryDependencies += "org.specs2" %% "specs2" % "2.3.11" % "test"

val monocleVersion = "1.1.1"

libraryDependencies ++= Seq(
  "com.github.julien-truffaut"  %%  "monocle-core"    % monocleVersion,
  "com.github.julien-truffaut"  %%  "monocle-law"     % monocleVersion % "test"
  //"com.github.julien-truffaut"  %%  "monocle-generic" % monocleVersion,
  //"com.github.julien-truffaut"  %%  "monocle-macro"   % monocleVersion,
)

//scalacOptions += "-Xlog-implicits"
