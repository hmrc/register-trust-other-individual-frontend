
# Migration to GovUk Frontend

Things to watch out for:

**Information pages with continue buttons as links**

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

**Update components**

We have
```aidl
@()(implicit messages: Messages)

<div class="js-visible">
  <p><a id="back-link" class="link-back" href="#">@messages("site.back")</a></p>
</div>
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