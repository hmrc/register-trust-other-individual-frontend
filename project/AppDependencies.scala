import sbt.*

object AppDependencies {
  val bootstrapVersion = "7.23.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "play-frontend-hmrc"             % "7.29.0-play-28",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping"  % "1.13.0-play-28",
    "uk.gov.hmrc"       %% "domain"                         % "8.3.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"     % bootstrapVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"              %% "bootstrap-test-play-28"   % bootstrapVersion,
    "org.scalatest"            %% "scalatest"               % "3.2.17",
    "org.scalatestplus"        %% "scalacheck-1-17"         % "3.2.17.0",
    "org.scalatestplus.play"   %% "scalatestplus-play"      % "5.1.0",
    "com.vladsch.flexmark"     %  "flexmark-all"            % "0.64.8",
    "org.jsoup"                %  "jsoup"                   % "1.17.2",
    "org.mockito"              %% "mockito-scala"           % "1.17.30",
    "io.github.wolfendale"     %% "scalacheck-gen-regexp"   % "1.1.0",
    "org.wiremock"             %  "wiremock-standalone"     % "3.3.1"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

}
