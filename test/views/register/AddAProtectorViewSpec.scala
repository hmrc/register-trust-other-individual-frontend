/*
 * Copyright 2020 HM Revenue & Customs
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

import forms.AddAProtectorFormProvider
import models.register.pages.AddAProtector
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.AddRow
import views.behaviours.{OptionsViewBehaviours, TabularDataViewBehaviours}
import views.html.register.AddAProtectorView

class AddAProtectorViewSpec extends OptionsViewBehaviours with TabularDataViewBehaviours {

  val featureUnavalible = "/trusts-registration/feature-not-available"

  val completeProtectors = Seq(
    AddRow("Business 1", "Business protector", featureUnavalible, featureUnavalible),
    AddRow("Business 2", "Business protector", featureUnavalible, featureUnavalible)
  )

  val inProgressProtectors = Seq(
    AddRow("Business 3", "Business protector", featureUnavalible, featureUnavalible),
    AddRow("Business 4", "Business protector", featureUnavalible, featureUnavalible)
  )
  val messageKeyPrefix = "addAProtector"

  val form = new AddAProtectorFormProvider()()

  val view = viewFor[AddAProtectorView](Some(emptyUserAnswers))

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, fakeDraftId, Nil, Nil, "Add a protector", Nil)(fakeRequest, messages)

  def applyView(form: Form[_], inProgressProtectros: Seq[AddRow], completeProtectros: Seq[AddRow], count : Int, maxedOut: List[String]): HtmlFormat.Appendable = {
    val title = if (count > 1) s"You have added $count protectors" else "You have added 1 protector"
    view.apply(form, fakeDraftId, inProgressProtectros, completeProtectros, title, maxedOut)(fakeRequest, messages)
  }

  "AddAProtectorView" when {

    "there is no protector data" must {

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithBackLink(applyView(form))

      behave like pageWithNoTabularData(applyView(form))

      behave like pageWithOptions(form, applyView, AddAProtector.options)
    }

    "there is data in progress" must {

      val viewWithData = applyView(form, inProgressProtectors, Nil, 2, Nil)

      behave like dynamicTitlePage(viewWithData, "addAProtector.count", "2")

      behave like pageWithBackLink(viewWithData)

      behave like pageWithInProgressTabularData(viewWithData, inProgressProtectors)

      behave like pageWithOptions(form, applyView, AddAProtector.options)
    }

    "there is complete data" must {

      val viewWithData = applyView(form, Nil, completeProtectors, 2, Nil)

      behave like dynamicTitlePage(viewWithData, "addAProtector.count", "2")

      behave like pageWithBackLink(viewWithData)

      behave like pageWithCompleteTabularData(viewWithData, completeProtectors)

      behave like pageWithOptions(form, applyView, AddAProtector.options)
    }

    "there is both in progress and complete data" must {

      val viewWithData = applyView(form, inProgressProtectors, completeProtectors, 4, Nil)

      behave like dynamicTitlePage(viewWithData, "addAProtector.count", "4")

      behave like pageWithBackLink(viewWithData)

      behave like pageWithTabularData(viewWithData, inProgressProtectors, completeProtectors)

      behave like pageWithOptions(form, applyView, AddAProtector.options)
    }

    "there is one maxed out protector" must {
      val viewWithData = applyView(form, inProgressProtectors, completeProtectors, 4, List("Protectors"))

      behave like dynamicTitlePage(viewWithData, "addAProtector.count", "4")

      behave like pageWithBackLink(viewWithData)

      behave like pageWithTabularData(viewWithData, inProgressProtectors, completeProtectors)

      behave like pageWithOptions(form, applyView, AddAProtector.options)

      "shows no radios and shows content for maxed protectors" in {
        val doc = asDocument(viewWithData)
        assertNotRenderedById(doc, "value")
        assertContainsText(doc, "You cannot add another protector as you have entered a maximum of 25.")
        assertContainsText(doc, "Check the protectors you have added. If you have further protectors to add, write to HMRC with their details.")
      }
    }
  }

}
