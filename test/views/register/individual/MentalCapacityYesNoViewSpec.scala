/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package views.register.individual

import forms.YesNoDontKnowFormProvider
import models.{FullName, YesNoDontKnow}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.RadioOption
import views.behaviours.{OptionsViewBehaviours, QuestionViewBehaviours}
import views.html.register.individual.MentalCapacityYesNoView

class MentalCapacityYesNoViewSpec extends QuestionViewBehaviours[YesNoDontKnow] with OptionsViewBehaviours {

  val prefix = "otherIndividual.5mld.mentalCapacityYesNo"
  val index = 0
  val name: String = FullName("FirstName", None, "LastName").toString

  val form: Form[YesNoDontKnow] = new YesNoDontKnowFormProvider().withPrefix(prefix)

  "mentalCapacityYesNoView view" must {

    val view = viewFor[MentalCapacityYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, fakeDraftId, index, name)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), prefix, name,"p1", "bulletpoint1", "bulletpoint2", "bulletpoint3", "bulletpoint4")

    behave like pageWithBackLink(applyView(form))

    val options = List(
      RadioOption(id = "value-yes", value = YesNoDontKnow.Yes.toString, messageKey = "site.yes"),
      RadioOption(id = "value-no", value = YesNoDontKnow.No.toString, messageKey = "site.no"),
      RadioOption(id = "value-dontKnow", value = YesNoDontKnow.DontKnow.toString, messageKey = "site.dontKnow")
    )

    behave like pageWithOptions[YesNoDontKnow](form, applyView, options)

    behave like pageWithASubmitButton(applyView(form))
  }
}