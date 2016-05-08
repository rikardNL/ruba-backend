import com.typesafe.sbt.packager.docker._

name := "ruba-backend"

resolvers += "twitter-repo" at "http://maven.twttr.com"

version := "0.2.3"

scalaVersion := "2.11.7"

val finchVersion      = "0.9.3"
val circeVersion      = "0.2.1"
val bijectionVersion  = "0.8.1"

libraryDependencies ++= Seq(
  "com.typesafe.slick"    %%    "slick"             %   "3.1.1",
  "com.typesafe"          %     "config"            %   "1.3.0",
  "com.twitter"           %%    "twitter-server"    %   "1.16.0",
  "com.github.finagle"    %%    "finch-core"        %   finchVersion,
  "com.github.finagle"    %%    "finch-circe"       %   finchVersion,
  "com.github.finagle"    %%    "finch-test"        %   finchVersion,
  "io.circe"              %%    "circe-generic"     %   circeVersion,
  "com.twitter"           %%    "bijection-core"    %   bijectionVersion,
  "com.twitter"           %%    "bijection-util"    %   bijectionVersion,
  "org.slf4j"             %     "slf4j-nop"         %   "1.6.4",
  "com.zaxxer"            %     "HikariCP"          %   "2.4.4",
  "org.postgresql"        %     "postgresql"        %   "9.4.1208",
  "org.flywaydb"          %     "flyway-core"       %   "4.0",
  "org.scalatest"         %%    "scalatest"         %   "2.2.5"             %   "test",
  "com.h2database"        %     "h2"                %   "1.4.187"           %   "test"
)

enablePlugins(JavaAppPackaging, DockerPlugin)

dockerBaseImage := "anapsix/alpine-java"

dockerExposedPorts := Seq(9090)
