# Migration to play-frontend-hmrc

## Contents:
1. [Resources](#resources)
1. [Things to watch out for](#things-to-watch-out-for)
   1. [Continue buttons as links](#information-pages-with-continue-buttons-as-links)
   1. [Position of components](#position-of-components)
   1. [Update components](#update-components)
1. [Tests](#tests)
1. [General tips](#general-tips)

###Resources

To see twirl examples of gov uk design system
https://github.com/hmrc/play-frontend-govuk-extension

Look at twirl components to get an idea what markup it will generate
https://github.com/hmrc/play-frontend-govuk/tree/master/src/main/twirl/uk/gov/hmrc/govukfrontend/views/components

Look at the scala view models to easily tell what keys and data types are required
https://github.com/hmrc/play-frontend-govuk/tree/master/src/main/scala/uk/gov/hmrc/govukfrontend/views/viewmodels

The folder pattern is the same between `play-frontend-govuk` and `play-frontend-hmrc`

Most standard components are gov-uk, but language select, timeout dialog and the add to list pattern are HMRC specific
https://github.com/hmrc/play-frontend-hmrc

###Things to watch out for:

####Information pages with continue buttons as links

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

####Position of components

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

####Update components

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

###Tests

####Unit tests

Where possible, try adding an id to components and have them match previous components so that tests don't need to be changed.

### General tips

If a component doesn't autogenerate an id or have a specific key, you can typically add one via `attributes`
```scala
attributes = Map("id" -> "test-id")
```