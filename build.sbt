import play.sbt.routes.RoutesKeys
import sbt.Def
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

lazy val appName: String = "register-trust-other-individual-frontend"

ThisBuild / scalaVersion := "2.13.14"
ThisBuild / majorVersion := 0

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin, SbtSassify)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    name := appName,
    RoutesKeys.routesImport += "models._",
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "play.twirl.api.HtmlFormat._",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._",
      "views.ViewUtils._",
      "models.Mode",
      "controllers.routes._"
    ),
    PlayKeys.playDefaultPort := 8841,
    ScoverageKeys.coverageExcludedFiles := "<empty>;.*components.*;.*Mode.*;.*Routes.*;",
    ScoverageKeys.coverageMinimumStmtTotal := 91,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    scalacOptions ++= Seq("-feature"),
    libraryDependencies ++= AppDependencies(),
    // concatenate js
    Concat.groups := Seq(
      "javascripts/registertrustotherindividualfrontend-app.js" ->
        group(Seq(
          "javascripts/registertrustotherindividualfrontend.js",
          "javascripts/autocomplete.js",
          "javascripts/iebacklink.js",
          "javascripts/print.js",
          "javascripts/libraries/location-autocomplete.min.js"
        ))
    ),
    // prevent removal of unused code which generates warning errors due to use of third-party libs
    uglifyCompressOptions := Seq("unused=false", "dead_code=false"),
    pipelineStages := Seq(digest),
    // below line required to force asset pipeline to operate in dev rather than only prod
    Assets / pipelineStages := Seq(concat,uglify),
    // only compress files generated by concat
    uglify / includeFilter := GlobFilter("registertrustotherindividualfrontend-*.js"),
    scalacOptions ++= Seq(
      "-feature",
      "-Wconf:src=routes/.*:s",
      "-Wconf:cat=unused-imports&src=views/.*:s"
    )
  )

addCommandAlias("scalastyleAll", "all scalastyle Test/scalastyle")
