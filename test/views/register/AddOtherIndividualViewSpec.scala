/*
 * Copyright 2021 HM Revenue & Customs
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

package views.register

import forms.AddOtherIndividualFormProvider
import models.register.pages.AddOtherIndividual
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.AddRow
import views.behaviours.{OptionsViewBehaviours, TabularDataViewBehaviours}
import views.html.register.AddOtherIndividualView

class AddOtherIndividualViewSpec extends OptionsViewBehaviours with TabularDataViewBehaviours {

  val featureUnavalible = "/trusts-registration/feature-not-available"

  val completeOtherIndividuals = Seq(
    AddRow("Business 1", "Business otherIndividual", featureUnavalible, featureUnavalible),
    AddRow("Business 2", "Business otherIndividual", featureUnavalible, featureUnavalible)
  )

  val inProgressOtherIndividuals = Seq(
    AddRow("Business 3", "Business otherIndividual", featureUnavalible, featureUnavalible),
    AddRow("Business 4", "Business otherIndividual", featureUnavalible, featureUnavalible)
  )
  val messageKeyPrefix = "addOtherIndividual"

  val form = new AddOtherIndividualFormProvider()()

  val view = viewFor[AddOtherIndividualView](Some(emptyUserAnswers))

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, fakeDraftId, Nil, Nil, "Add other individual", false)(fakeRequest, messages)

  def applyView(form: Form[_], inProgressProtectros: Seq[AddRow], completeProtectros: Seq[AddRow], count : Int, maxedOut: Boolean): HtmlFormat.Appendable = {
    val title = if (count > 1) s"You have added $count other individuals" else "You have added 1 otherIndividual"
    view.apply(form, fakeDraftId, inProgressProtectros, completeProtectros, title, maxedOut)(fakeRequest, messages)
  }

  "AddOtherIndividualView" when {

    "there is no otherIndividual data" must {

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithBackLink(applyView(form))

      behave like pageWithNoTabularData(applyView(form))

      behave like pageWithOptions(form, applyView, AddOtherIndividual.options)
    }

    "there is data in progress" must {

      val viewWithData = applyView(form, inProgressOtherIndividuals, Nil, 2, false)

      behave like dynamicTitlePage(viewWithData, "addOtherIndividual.count", "2")

      behave like pageWithBackLink(viewWithData)

      behave like pageWithInProgressTabularData(viewWithData, inProgressOtherIndividuals)

      behave like pageWithOptions(form, applyView, AddOtherIndividual.options)
    }

    "there is complete data" must {

      val viewWithData = applyView(form, Nil, completeOtherIndividuals, 2, false)

      behave like dynamicTitlePage(viewWithData, "addOtherIndividual.count", "2")

      behave like pageWithBackLink(viewWithData)

      behave like pageWithCompleteTabularData(viewWithData, completeOtherIndividuals)

      behave like pageWithOptions(form, applyView, AddOtherIndividual.options)
    }

    "there is both in progress and complete data" must {

      val viewWithData = applyView(form, inProgressOtherIndividuals, completeOtherIndividuals, 4, false)

      behave like dynamicTitlePage(viewWithData, "addOtherIndividual.count", "4")

      behave like pageWithBackLink(viewWithData)

      behave like pageWithTabularData(viewWithData, inProgressOtherIndividuals, completeOtherIndividuals)

      behave like pageWithOptions(form, applyView, AddOtherIndividual.options)
    }

    "there is one maxed out otherIndividual" must {
      val viewWithData = applyView(form, inProgressOtherIndividuals, completeOtherIndividuals, 4, true)

      behave like dynamicTitlePage(viewWithData, "addOtherIndividual.count", "4")

      behave like pageWithBackLink(viewWithData)

      behave like pageWithTabularData(viewWithData, inProgressOtherIndividuals, completeOtherIndividuals)

      behave like pageWithOptions(form, applyView, AddOtherIndividual.options)

      "shows no radios and shows content for maxed otherIndividuals" in {
        val doc = asDocument(viewWithData)
        assertNotRenderedById(doc, "value")
        assertContainsText(doc, "You cannot add another other individual as you have entered a maximum of 25.")
        assertContainsText(doc, "Check the other individuals you have added. If you have further other individuals to add, write to HMRC with their details.")
      }
    }
  }

}
