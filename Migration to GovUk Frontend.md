# Migration to play-frontend-hmrc

## Contents:
1. [Resources](#resources)
   1. [Twirl chrome extension](#twirl-chrome-extension)
   1. [Components](#components)
   1. [Remove old references and styles](#remove-old-references-and-styles)
1. [Things to watch out for](#things-to-watch-out-for)
   1. [Duplicate components](#duplicate-components)
   1. [Back link](#back-link)
   1. [Continue buttons as links](#information-pages-with-continue-buttons-as-links)
   1. [Date and error summary](#date-and-error-summary)
   1. [Radio options](#radio-options)
   1. [Extra classes for components](#extra-classes-for-components)
   1. [Accessible autocomplete](#accessible-autocomplete)
   1. [Deskpro link](#Deskpro-link)
   3. [Check your answers](#check-your-answers)
   4. [Add to list maximum state](#Add-to-list-maximum-state)
   1. [Draft and confirm Print](#draft-and-confirm-print)
1. [Tests](#tests)
1. [General tips](#general-tips)



## Resources

> Start with this README.md:
https://github.com/hmrc/play-frontend-hmrc

For the layout, you can copy and paste the MainTemplate from this repo.

### Twirl Chrome extension
:gear:
Installing the below extension allows you to view a twirl example of the components at https://design-system.service.gov.uk/components/button/

> To see twirl examples of gov uk design system
https://github.com/hmrc/play-frontend-govuk-extension

### Components

> Look at twirl components to get an idea what markup it will generate
https://github.com/hmrc/play-frontend-govuk/tree/master/src/main/twirl/uk/gov/hmrc/govukfrontend/views/components

> Look at the scala view models to easily tell what keys and data types are required
https://github.com/hmrc/play-frontend-govuk/tree/master/src/main/scala/uk/gov/hmrc/govukfrontend/views/viewmodels

The folder pattern is the same between `play-frontend-govuk` and `play-frontend-hmrc`

Most standard components are gov-uk, but language select, timeout dialog and the add to list pattern are HMRC specific.

### Remove old references and styles

Remove any references to assets-frontend & play-ui

in conf/application.conf:

`conf/application.conf`
```diff
- #Needed by play-ui to disable google analytics as we use gtm via HeadWithTrackConsent
- google-analytics.token = "N/A"

-assets {
-  version = "3.11.0"
-  version = ${?ASSETS_FRONTEND_VERSION}
-  url     = "http://localhost:9032/assets/"
-}
```
`app/config/FrontendAppConfig.scala`
```diff
- val analyticsToken: String = configuration.get[String](s"google-analytics.token")
```

The old govuk-template https://github.com/hmrc/govuk-template is no longer required as a GovUkLayout is now provided by play-frontend-govuk.

`conf/prod.routes`
```diff
- ->                /template                  template.Routes
```

`project/AppDependencies/scala`
```diff
-    "uk.gov.hmrc"       %% "govuk-template"                 % "5.63.0-play-27"
```

Remove GovUkWrapper as everything should now be in MainTemplate.scala.html

Replace all of the old components with the components in this repo

Remove everything in the stylesheet folder apart from the location-autocomplete.min.scss

#### Stylesheets

`app/assets/stylesheets/application.scss` can be copied over from this service to include the relevant fixes. If doing this manually you will need the following code in order to apply the "govuk-body" class to all paragraphs automatically:
```diff
@import "lib/govuk-frontend/govuk/base";
@import "lib/govuk-frontend/govuk/core/typography";

p {
  @extend .govuk-body;
}


h2 {
 @extend .govuk-heading-m
}
```

## Things to watch out for:

#### Duplicate components

> If the same new component is used multiple times to ‘copy’ old components, consider how easy it is simplify and use less components. We should aim for fewer components, but will decide on a case by case basis.


### Back Link
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
```diff
+ @this( ...
+ hmrcLanguageSelectHelper: HmrcLanguageSelectHelper,
+ govukBackLink: GovukBackLink
+ )

+ @( ...
+ showBackLink: Boolean = false
+ )

+ @beforeContentBlock = {
+   @hmrcLanguageSelectHelper()
+   @if(showBackLink) {
+      @govukBackLink(BackLink(
+         attributes = Map("id" -> "back-link"), classes="js-visible", href="javascript:history.back()", content = HtmlContent(messages("site.back"))
+      ))
+   }
+ }

+ @govukLayout( ...
+    beforeContentBlock = Some(beforeContentBlock),
+ )


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
`app/assets/stylesheets/application.scss`
```diff
+ // ----------------
+ // Hide the back link when body does not have .js-enabled
+ //
+ // ----------------
+ 
+ body:not(.js-enabled) {
+     .govuk-back-link {
+         display: none;
+         visibility: hidden;
+         width: 0;
+         height: 0;
+     }
+ }
```

which hides the back link if the body does not have a css class .js-enabled (set by govuk-frontend).

This is supported by all major browsers back to IE9.
https://developer.mozilla.org/en-US/docs/Web/CSS/:not

#### Back link support in Internal Explorer (experimental) 
`app/assets/javascripts/iebacklink.js`
```diff
+ $(document).ready(function() {
+     // =====================================================
+     // Back link mimics browser back functionality
+     // =====================================================
+     // store referrer value to cater for IE - https://developer.microsoft.com/en-us/microsoft-edge/platform/issues/10474810/  */
+     var docReferrer = document.referrer
+     // prevent resubmit warning
+     if (window.history && window.history.replaceState && typeof window.history.replaceState === 'function') {
+         window.history.replaceState(null, null, window.location.href);
+     }
+     $('#back-link').on('click', function(e){
+         e.preventDefault();
+         window.history.back();
+     });
+ });

```

`build.sbt`
```diff
    // concatenate js
    Concat.groups := Seq(
      "javascripts/registertrustotherindividualfrontend-app.js" ->
        group(Seq(
          "javascripts/registertrustotherindividualfrontend.js",
          "javascripts/autocomplete.js",
+         "javascripts/iebacklink.js",
          "javascripts/libraries/location-autocomplete.min.js"
        ))
    ),
```

**[Back to top](#contents)**


### Information pages with continue buttons as links

We have
```scala
@components.button_link(messages("site.continue"), NameController.onPageLoad(0, draftId).url)
```
and changed to
```diff
+    @formHelper(action = InfoController.onSubmit(draftId), 'autoComplete -> "off") {
        @submit_button(Some(messages("site.continue")))
    }
    
+  POST       /:draftId/information-you-need               controllers.register.InfoController.onSubmit(draftId: String)

+  def onSubmit(draftId: String) = standardActionSets.identifiedUserWithData(draftId) {
    implicit request =>
      Redirect(controllers.register.individual.routes.NameController.onPageLoad(0, draftId))
  }
```

**[Back to top](#contents)**

### Date and error summary

Have made changes to ViewUtils.errorHref and DateErrorFormatter.formatArgs for use in DateInput and ErrorSummary.

**Note:** The changes to errorHref make error highlighting work for Yes/No Radio buttons only if the message keys used on these pages have "YesNo" in the message Key so any that don't use this format will need to be changed. 

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

@(errors: Seq[FormError], radioOptions: Seq[RadioOption] = Nil)(implicit messages: Messages)

@if(errors.nonEmpty) {
    @govUkErrorSummary(ErrorSummary(
        errorList = errors.map(err => err.copy(key = errorHref(err, radioOptions), args = formatArgs(err.args))).asTextErrorLinks,
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
unless it's a radio option, then also include options that match `mapRadioOptionsToRadioItems(form("value"), false, KindOfBusiness.options),`

```
        @error_summary(form.errors, KindOfBusiness.options)
```

Changes to ViewUitls:

Updated errorHref:

```scala
  def errorHref(error: FormError, radioOptions: Seq[RadioOption] = Nil): String = {
    error.args match {
      case x if x.contains("day") || x.contains("month") || x.contains("year") =>
        s"${error.key}.${error.args.head}"
      case _ if error.message.toLowerCase.contains("yesno") =>
        s"${error.key}-yes"
      case _ if radioOptions.size != 0 =>
        radioOptions.head.id
      case _ =>
        val isSingleDateField = error.message.toLowerCase.contains("date") && !error.message.toLowerCase.contains("yesno")
        if (error.key.toLowerCase.contains("date") || isSingleDateField) {
          s"${error.key}.day"
        } else {
          s"${error.key}"
        }
    }
  }
```
Added mapRadioOptionsToRadioItems:

```scala
  def mapRadioOptionsToRadioItems(field: Field, trackGa: Boolean,
                                  inputs: Seq[RadioOption])(implicit messages: Messages): Seq[RadioItem] =
    inputs.map(
      a => {
        RadioItem(
          id = Some(a.id),
          value = Some(a.value),
          checked = field.value.contains(a.value),
          content = Text(messages(a.messageKey)),
          attributes = if (trackGa) Map[String, String]("data-journey-click" -> s"trusts-frontend:click:${a.id}") else Map.empty
        )
      }
    )
```
**[Back to top](#contents)**

### Radio options

Add app/views/components/InputRadio.scala.html

Changes to 'Add to' view:

```diff
- @error_summary(form.errors)
+ @errorSummary(form.errors, AddOtherIndividual.options)

-  @components.input_radio(
-     field = form("value"),
-     legend = messages("addOtherIndividual.additional-content"),
-     legendClass = Some("heading-medium"),
-     inputs = AddOtherIndividual.options,
-     legendAsH2Heading = true
-  )
+  @input_radio(
+     field = form("value"),
+     legend = messages("addOtherIndividual.additional-content"),
+     headingIsLegend = false,
+     inputs = mapRadioOptionsToRadioItems(form("value"), false, AddOtherIndividual.options),
+     legendClass = Some("govuk-fieldset__legend--m")
+   )
```
Note: See update to ViewUtils above to add mapRadioOptionsToRadioItems method.

**[Back to top](#contents)**

### Extra classes for components

When using the new components, the default look may not match our design but we can add classes to get a close match. This affects the Name View The easiest way to check is to have it side by side with staging, as the old mark up won't always translate across.

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

**[Back to top](#contents)**


### Accessible autocomplete

Referring to the documentation at https://github.com/alphagov/accessible-autocomplete.

I took the latest CSS styling from https://github.com/alphagov/accessible-autocomplete/blob/master/dist/accessible-autocomplete.min.css and included them in the project at `app/assets/stylesheets/location-autocomplete.scss`. 

Imported the styles into `application.scss`:
```diff
+ @import location-autocomplete.mind
```

Enabled the sbt-sassify plugin in `build.sbt`:
```diff
+ .enablePlugins(PlayScala, SbtAutoBuildPlugin, SbtDistributablesPlugin, SbtArtifactory, SbtSassify)
```

Allowed `code.query.com` through the content security policy:
```diff
+ play.filters.headers.contentSecurityPolicy = "default-src 'self' 'unsafe-inline' localhost:8841 localhost:9032 localhost:9250 localhost:12345 www.google-analytics.com www.googletagmanager.com tagmanager.google.com 'self' data: ssl.gstatic.com www.gstatic.com fonts.gstatic.com fonts.googleapis.com code.jquery.com;"
```

The now unused key `contact-frontend.host` will need to be removed from `app-config-development`, `app-config-qa` `app-config-staging`, `app-config-production`.

The full extent of changes can be found at https://github.com/hmrc/register-trust-other-individual-frontend/commit/ecd03b41b7e4913aac684f1671b238b7fd2f9863

**[Back to top](#contents)**

### Deskpro link

The implementation for 'get help with this page' has been updated.

Commit for change can be seen at https://github.com/hmrc/register-trust-other-individual-frontend/commit/c3a43a213ec5673f1aec13cc25728961011b5f48

The main changes are:
- Include `@hmrcReportTechnicalIssueHelper()` in `MainTemplate.scala.html`
- Remove old config for contact-frontend from application.conf and replace with `contact-frontend.serviceId = "trusts"`
- Remove the following from `FrontendAppConfig.scala`:
```diff
- private val contactHost = configuration.get[String]("contact-frontend.host")
- private val contactFormServiceIdentifier = "trusts"
```

- Add `contactFrontendConfig: ContactFrontendConfig` as a injected dependency to `FrontendAppConfig.scala`.
- Remove values `reportAProblemPartialUrl` and `reportAProblemNonJSUrl` as play-frontend-govuk not takes care of the configuration
- Replace feedback urls with:
```diff
+ val betaFeedbackUrl = s"${contactFrontendConfig.baseUrl}/contact/beta-feedback?service=${contactFrontendConfig.serviceId}"
+  val betaFeedbackUnauthenticatedUrl = s"${contactFrontendConfig.baseUrl}/contact/beta-feedback-unauthenticated?service=${contactFrontendConfig.serviceId}"
```


**[Back to top](#contents)**


### Check Your Answers

Add app/utils/SectionFormatter.scala

Add GovukSummaryList component to Check Your Answers view:

```diff
 @this(
      main_template: MainTemplate,
      formHelper: FormWithCSRF,
      submit_button: SubmitButton
  )
 
 @(answerSection: AnswerSection, index: Int, draftId: String)(implicit request: Request[_], messages: Messages)
 
 @components.answer_section(answerSection)
``` 

Change to:

```diff
 @this(
     main_template: MainTemplate,
     formHelper: FormWithCSRF,
+     govukSummaryList: GovukSummaryList,
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

```diff
+ Ok(view(Seq(section), index, draftId))
``` 

**[Back to top](#contents)**

### Add to list maximum state

The maximum state for Add-to-page needs to be updated in the markup as the panel-indent + p spacing is off using govuk-frontend.

```diff
-        <ul>
-            <li class="panel-indent"><p>@messages("addOtherIndividual.maxedOut")</p></li>
-            <li class="panel-indent"><p>@messages("addOtherIndividual.maxedOut.paragraph")</p></li>
-        </ul>
+        <div class="govuk-inset-text">
+            <ul class="govuk-list">
+                <li>@messages("addOtherIndividual.maxedOut")</li>
+                <li>@messages("addOtherIndividual.maxedOut.paragraph")</li>
+            </ul>
+        </div>
```

**[Back to top](#contents)**

### Draft and confirm print

If you change a message key in the frontend such as:
```diff
- setUpAfterSettlorDied.checkYourAnswersLabel = ...
+ setUpAfterSettlorDiedYesNo.checkYourAnswersLabel = ...
```

Then you also need to update the message key in [trusts-frontend](https://github.com/hmrc/trusts-frontend) or [maintain-a-trust-frontend](https://github.com/hmrc/maintain-a-trust-frontend). This will ensure the draft and confirm print pages are up-to-date.

**[Back to top](#contents)**

## Tests

### Unit tests

Where possible, try adding an id to components and have them match previous components so that tests don't need to be changed.

Will need to update (due to default in a new component):
- `id="error-summary-heading"` is now `id="error-summary-title"` 
- For label errors `class="error-message"` has changed to `class="govuk-error-message"`
- `class="visually-hidden"` has changed to `class="govuk-visually-hidden"`
- `class="form-label"` has changed to `class="govuk-label"`
- `"form-hint"` has changed to `"govuk-hint"`
- `assertRenderedById(doc, "cymraeg-switch")` has changed to  `assertRenderedByCssSelector(doc, "a[lang=cy]")`
- <legend> now has `class="govuk-fieldset__legend` where there was no class before

 Banner title was
   
   ```
   val nav = doc.getElementById("proposition-menu")
          val span = nav.children.first
          span.text mustBe messages("site.service_name")
   ```
   
   and is now
   
   ```
          val bannerTitle = doc.getElementsByClass("govuk-header__link govuk-header__link--service-name")
          bannerTitle.html() mustBe messages("service.name")
   ```
   
   
 Changes to ViewSpecBase.assertContainsRadioButton:

```diff
-    if (isChecked) {
-      assert(radio.attr("checked") == "checked", s"\n\nElement $id is not checked")
-    } else {
-      assert(!radio.hasAttr("checked") && radio.attr("checked") != "checked", s"\n\nElement $id is checked")
-    }
+    isChecked match {
+      case true => assert(radio.hasAttr("checked"), s"\n\nElement $id is not checked")
+      case _ => assert(!radio.hasAttr("checked"), s"\n\nElement $id is checked")
+    }
```
   
### General tips

If a component doesn't autogenerate an id or have a specific key, you can typically add one via `attributes`
```scala
attributes = Map("id" -> "test-id")
```
   
**[Back to top](#contents)**
