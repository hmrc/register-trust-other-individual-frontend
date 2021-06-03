# Migration to play-frontend-hmrc

## Contents:
1. [Resources](#resources)
1. [Things to watch out for](#things-to-watch-out-for)
   1. [Continue buttons as links](#information-pages-with-continue-buttons-as-links)
   1. [Position of components](#position-of-components)
   1. [Update components](#update-components)
   1. [Classes for components](#classes-for-components)
   1. [Accessible autocomplete](#accessible-autocomplete)
1. [Tests](#tests)
1. [General tips](#general-tips)

### Resources

To see twirl examples of gov uk design system
https://github.com/hmrc/play-frontend-govuk-extension

Look at twirl components to get an idea what markup it will generate
https://github.com/hmrc/play-frontend-govuk/tree/master/src/main/twirl/uk/gov/hmrc/govukfrontend/views/components

Look at the scala view models to easily tell what keys and data types are required
https://github.com/hmrc/play-frontend-govuk/tree/master/src/main/scala/uk/gov/hmrc/govukfrontend/views/viewmodels

The folder pattern is the same between `play-frontend-govuk` and `play-frontend-hmrc`

Most standard components are gov-uk, but language select, timeout dialog and the add to list pattern are HMRC specific
https://github.com/hmrc/play-frontend-hmrc

### Things to watch out for:

#### Information pages with continue buttons as links

We have
```scala
@components.button_link(messages("site.continue"), NameController.onPageLoad(0, draftId).url)
```
and changed to
```scala
    @formHelper(action = InfoController.onSubmit(draftId), 'autoComplete -> "off") {
        @submit_button(Some(messages("site.continue")))
    }
    
++  POST       /:draftId/information-you-need               controllers.register.InfoController.onSubmit(draftId: String)

++  def onSubmit(draftId: String) = standardActionSets.identifiedUserWithData(draftId) {
    implicit request =>
      Redirect(controllers.register.individual.routes.NameController.onPageLoad(0, draftId))
  }
```

#### Position of components

#### Back Link
We have a back link component imported into a view
```scala
// Component

@()(implicit messages: Messages)

<div class="js-visible">
  <p><a id="back-link" class="link-back" href="#">@messages("site.back")</a></p>
</div>

// Imported and used in a view

@this (back_link: back_link)

@component.back_link
```
We can't just replace this component as it will render it in the `<main>` tag and won't be skipped by the skip link.
So now has been moved into the `MainTemplate.scala.html` in a `@beforeContentBlock` after the language toggle
```scala
@this( ...
hmrcLanguageSelectHelper: HmrcLanguageSelectHelper,
govukBackLink: GovukBackLink
)

@( ...
showBackLink: Boolean = false
)

@beforeContentBlock = {
   @hmrcLanguageSelectHelper()
   @if(showBackLink) {
      @govukBackLink(BackLink(
         attributes = Map("id" -> "back-link"), classes="js-visible", href="javascript:history.back()", content = HtmlContent(messages("site.back"))
      ))
   }
}

@govukLayout( ...
    beforeContentBlock = Some(beforeContentBlock),
)


```
and in the view change to
```scala
@main_template(
    title = s"${errorPrefix(form)}${messages("trustHasOtherIndividualYesNo.title")}",
    showBackLink = true
) {

```

#### Visibility of the back link

A CSS rule has been added 
```css
// ----------------
// Hide the back link when body does not have .js-enabled
//
// ----------------

body:not(.js-enabled) {
    .govuk-back-link {
        display: none;
        visibility: hidden;
        width: 0;
        height: 0;
    }
}
```

which hides the back link if the body does not have a css class .js-enabled (set by govuk-frontend).

This is supported by all major browsers back to IE9.
https://developer.mozilla.org/en-US/docs/Web/CSS/:not

#### Update components

> If the same new component is used multiple times to ‘copy’ old components, consider how easy it is simplify and use less components. We should aim for fewer components, but will decide on a case by case basis.

Have made changes to ViewUtils.errorHref and DateErrorFormatter.formatArgs for use in DateInput and ErrorSummary

We have for error summary

```html
@import utils.DateErrorFormatter._

@(errors: Seq[FormError])(implicit messages: Messages)
@if(errors.nonEmpty) {
    <div id="errors" class="error-summary error-summary--show" role="alert" tabindex="-1">

        <h2 class="heading-medium error-summary-heading" id="error-summary-heading">
        @messages("error.summary.title")
        </h2>

        <ul role="list" class="error-summary-list">
            @for(error <- errors) {
                <li><a href="#@{errorHref(error)}">@messages(error.message, formatArgs(error.args):_*)</a></li>
            }
        </ul>

    </div>
}

```

changed to

```scala

@import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
@import utils.DateErrorFormatter._

@this(
    govUkErrorSummary: GovukErrorSummary,
)

@(errors: Seq[FormError])(implicit messages: Messages)

@if(errors.nonEmpty) {
    @govUkErrorSummary(ErrorSummary(
        errorList = errors.map(err => err.copy(args = formatArgs(err.args))).asTextErrorLinks,
        title = Text(messages("error.summary.title"))
    ))
}
```

and in view changed to

```scala
@this( ...
   error_summary: ErrorSummary
)

@error_summary(form.errors)
```

#### Classes for components

When using the new components, the default look may not match our design but we can add classes to get a close match. The easiest way to check is to have it side by side with staging, as the old mark up won't always translate across.

[Check the component in the Design System](https://design-system.service.gov.uk/components/) first, classes found under the HTML tab.

Some generic styles
https://design-system.service.gov.uk/styles/typography/

```
 // gov-uk default input
      @input_text(
          field = form("middleName"),
          label = messages("otherIndividual.name.middleName"),
          autocomplete = Some("additional-name")
      )
    
 // adding classes from https://design-system.service.gov.uk/components/text-input/
      @input_text(
          field = form("firstName"),
          label = messages("otherIndividual.name.firstName"),
          labelClass = Some("govuk-label--s"),
          inputClass = Some("govuk-!-width-one-half"),
          autocomplete = Some("given-name")
      )

     
 ```

#### Accessible autocomplete

Referring to the documentation at https://github.com/alphagov/accessible-autocomplete.

I took the latest CSS styling from https://github.com/alphagov/accessible-autocomplete/blob/master/dist/accessible-autocomplete.min.css and included them in the project at `app/assets/stylesheets/location-autocomplete.scss`. Imported the styles into application.scss:
```css
@import location-autocomplete.mind
```

Enabled the sbt-sassify plugin in `build.sbt`:
```scala
.enablePlugins(PlayScala, SbtAutoBuildPlugin, SbtDistributablesPlugin, SbtArtifactory, SbtSassify)
```

Allowed `code.query.com` through the content security policy:
```
play.filters.headers.contentSecurityPolicy = "default-src 'self' 'unsafe-inline' localhost:8841 localhost:9032 localhost:9250 localhost:12345 www.google-analytics.com www.googletagmanager.com tagmanager.google.com 'self' data: ssl.gstatic.com www.gstatic.com fonts.gstatic.com fonts.googleapis.com code.jquery.com;"
```

This **will** need updated in `app-config-base`.

The full extent of changes can be found at https://github.com/hmrc/register-trust-other-individual-frontend/commit/ecd03b41b7e4913aac684f1671b238b7fd2f9863

#### Get help with this page (Deskpro)

The implementation for 'get help with this page' has been updated.

Commit for change can be seen at https://github.com/hmrc/register-trust-other-individual-frontend/commit/c3a43a213ec5673f1aec13cc25728961011b5f48

The main changes are:
- Include `@hmrcReportTechnicalIssueHelper()` in `MainTemplate.scala.html`
- Remove old config for contact-frontend from application.conf and replace with `contact-frontend.serviceId = "trusts"`
- Remove the following from `FrontendAppConfig.scala`:
```scala
private val contactHost = configuration.get[String]("contact-frontend.host")
private val contactFormServiceIdentifier = "trusts"
```

- Add `contactFrontendConfig: ContactFrontendConfig` as a injected dependency to `FrontendAppConfig.scala`.
- Remove values `reportAProblemPartialUrl` and `reportAProblemNonJSUrl` as play-frontend-govuk not takes care of the configuration
- Replace feedback urls with:
```scala
  val betaFeedbackUrl = s"${contactFrontendConfig.baseUrl}/contact/beta-feedback?service=${contactFrontendConfig.serviceId}"
  val betaFeedbackUnauthenticatedUrl = s"${contactFrontendConfig.baseUrl}/contact/beta-feedback-unauthenticated?service=${contactFrontendConfig.serviceId}"
```

#### Check Your Answers

Add app/utils/SectionFormatter.scala

Add GovukSummaryList component to Check Your Answers view:

```scala
 @this(
     main_template: MainTemplate,
     formHelper: FormWithCSRF,
     submit_button: SubmitButton
 )

@(answerSection: AnswerSection, index: Int, draftId: String)(implicit request: Request[_], messages: Messages)

@components.answer_section(answerSection)

``` 

Change to:

```scala
 @this(
     main_template: MainTemplate,
     formHelper: FormWithCSRF,
     govukSummaryList: GovukSummaryList,
     submit_button: SubmitButton
 )
 
@(answerSection: Seq[Section], index: Int, draftId: String)(implicit request: Request[_], messages: Messages)

@govukSummaryList(SummaryList(rows = formatSections(answerSection)))

``` 

Update Check Your Answers controller:

```scala
Ok(view(section, index, draftId))
``` 
Change to:

```scala
Ok(view(Seq(section), index, draftId))
``` 

#### Unused configuration in application.conf

Remove:

`conf/application.conf`
```diff
- #Needed by play-ui to disable google analytics as we use gtm via HeadWithTrackConsent
- google-analytics.token = "N/A"
```
`app/config/FrontendAppConfig.scala`
```diff
- val analyticsToken: String = configuration.get[String](s"google-analytics.token")
```

### Tests

#### Unit tests

Where possible, try adding an id to components and have them match previous components so that tests don't need to be changed.

Will need to update (due to default in a new component):
- `id="error-summary-heading"` is now `id="error-summary-title"` 
- For label errors `class="error-message"` has changed to `class="govuk-error-message"`
- `class="visually-hidden"` has changed to `class="govuk-visually-hidden"`
- `class="form-label"` has changed to `class="govuk-label"`
- <legend> now has `class="govuk-fieldset__legend` where there was no class before

### General tips

If a component doesn't autogenerate an id or have a specific key, you can typically add one via `attributes`
```scala
attributes = Map("id" -> "test-id")
```