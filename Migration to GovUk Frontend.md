
# Migration to GovUk Frontend

## Contents:
1. [Resources](#resources)
1. [Things to watch out for](#things-to-watch-out-for)
    1. [Continue buttons as links](#information-pages-with-continue-buttons-as-links)
    1. [Position of components](#position-of-components)
    1. [Update components](#update-components)

###Resources


###Things to watch out for:

####Information pages with continue buttons as links

We have
```aidl
@components.button_link(messages("site.continue"), NameController.onPageLoad(0, draftId).url)
```
and changed to
```aidl
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
```aidl
@()(implicit messages: Messages)

<div class="js-visible">
  <p><a id="back-link" class="link-back" href="#">@messages("site.back")</a></p>
</div>

[Imported and used in a view]

@this (back_link: back_link)

@component.back_link
```

and changed to
```aidl
@this(govukBackLink: GovukBackLink)

@()(implicit messages: Messages)

@govukBackLink(BackLink(href="javascript:history.back()", content = HtmlContent(messages("site.back"))))
```
and in the view change to
```aidl
@import views.html.components.BackLink

@this(backLink: BackLink)

@backLink()
```

####Update components

We have