import uk.gov.hmrc.DefaultBuildSettings.targetJvm

ThisBuild / scalaVersion := "3.9.0"
ThisBuild / majorVersion := 1
ThisBuild / targetJvm := "jvm-21"

lazy val microservice = Project("register-trust-other-individual-frontend", file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) // Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    CodeCoverageSettings(),
    scalacOptions ++= Seq(
      "-Wconf:msg=unused import&src=conf/.*:s",
      "-Wconf:msg=unused import&src=routes/.*:s",
      "-Wconf:msg=unused import&src=html/.*:s",
      "-Wconf:msg=unused import&src=views/.*:s",
      "-Wconf:src=routes/.*:s",
      "-Wconf:msg=Flag.*repeatedly:s",
      "-feature"
    ),
    routesImport += "models._",
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "play.twirl.api.HtmlFormat._",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._",
      "views.ViewUtils._",
      "controllers.routes._"
    ),
    PlayKeys.playDefaultPort := 8841,
    libraryDependencies ++= AppDependencies()
  )

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt")
