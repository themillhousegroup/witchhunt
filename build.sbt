name := "witchhunt"

// If the CI supplies a "build.version" environment variable, inject it as the rev part of the version number:
version := s"${sys.props.getOrElse("build.majorMinor", "0.1")}.${sys.props.getOrElse("build.version", "SNAPSHOT")}"

scalaVersion := "2.11.7"

organization := "com.themillhousegroup"

libraryDependencies ++= Seq(
    "com.themillhousegroup"       %%  "scoup"                 % "0.2.295",
    "com.helger"                  %   "ph-css"                % "4.1.1",
    "ch.qos.logback"              %   "logback-classic"       % "1.1.5",
    "com.typesafe.scala-logging"  %%  "scala-logging"         % "3.1.0",
    "com.github.scopt"            %%  "scopt"                 % "3.4.0",
    "com.typesafe.play"           %%  "play-json"             % "2.5.3",
    "org.mockito"                 %   "mockito-all"           % "1.10.19"       % "test",
    "org.specs2"                  %%  "specs2"                % "2.3.13"      % "test"
)

resolvers ++= Seq(  "oss-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
                    "oss-releases"  at "https://oss.sonatype.org/content/repositories/releases",
                    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/")

jacoco.settings

publishArtifact in (Compile, packageDoc) := false

seq(bintraySettings:_*)

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

scalariformSettings

net.virtualvoid.sbt.graph.Plugin.graphSettings

enablePlugins(JavaAppPackaging)

