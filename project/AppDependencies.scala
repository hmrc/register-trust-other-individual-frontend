import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "play-frontend-hmrc"             % "6.2.0-play-28",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.12.0-play-28",
    "uk.gov.hmrc"       %% "domain"                         % "8.1.0-play-28",
    "com.typesafe.play" %% "play-json-joda"                 % "2.9.4",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"     % "7.8.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"            %% "scalatest"               % "3.2.15",
    "org.scalatestplus"        %% "scalacheck-1-17"         % "3.2.15.0",
    "org.scalatestplus.play"   %% "scalatestplus-play"      % "5.1.0",
    "com.vladsch.flexmark"     %  "flexmark-all"            % "0.62.2",
    "org.jsoup"                %  "jsoup"                   % "1.15.3",
    "com.typesafe.play"        %% "play-test"               % PlayVersion.current,
    "org.mockito"              %% "mockito-scala"           % "1.17.12",
    "org.scalacheck"           %% "scalacheck"              % "1.17.0",
    "io.github.wolfendale"     %% "scalacheck-gen-regexp"   % "1.1.0",
    "com.github.tomakehurst"   %  "wiremock-standalone"     % "2.27.2"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

}
