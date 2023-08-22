import sbt._

object AppDependencies {
  import play.core.PlayVersion
  val bootstrapVersion = "7.21.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "play-frontend-hmrc"             % "7.19.0-play-28",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.13.0-play-28",
    "uk.gov.hmrc"       %% "domain"                         % "8.3.0-play-28",
    "com.typesafe.play" %% "play-json-joda"                 % "2.9.4",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"     % bootstrapVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"              %% "bootstrap-test-play-28"   % bootstrapVersion,
    "org.scalatest"            %% "scalatest"               % "3.2.16",
    "org.scalatestplus"        %% "scalacheck-1-17"         % "3.2.16.0",
    "org.scalatestplus.play"   %% "scalatestplus-play"      % "5.1.0",
    "com.vladsch.flexmark"     %  "flexmark-all"            % "0.64.8",
    "org.jsoup"                %  "jsoup"                   % "1.16.1",
    "com.typesafe.play"        %% "play-test"               % PlayVersion.current,
    "org.mockito"              %% "mockito-scala"           % "1.17.14",
    "org.scalacheck"           %% "scalacheck"              % "1.17.0",
    "io.github.wolfendale"     %% "scalacheck-gen-regexp"   % "1.1.0",
    "com.github.tomakehurst"   %  "wiremock-standalone"     % "2.27.2"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

}
