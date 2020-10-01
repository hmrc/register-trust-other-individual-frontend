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

package controllers.register

import base.SpecBase
import controllers.register.individual.{routes => irts}
import forms.{AddOtherIndividualFormProvider, YesNoFormProvider}
import models.Status.Completed
import models.register.pages.AddOtherIndividual
import models.{FullName, UserAnswers}
import pages.entitystatus.OtherIndividualStatus
import pages.register.individual._
import pages.register.{AddOtherIndividualPage, TrustHasOtherIndividualYesNoPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.AddRow
import views.html.register.{AddOtherIndividualView, TrustHasOtherIndividualYesNoView}

class AddOtherIndividualControllerSpec extends SpecBase {

  private val index: Int = 0
  private val max: Int = 25

  private def removeOtherIndividualRoute(index: Int): String =
    controllers.register.individual.routes.RemoveOtherIndividualController.onPageLoad(index, fakeDraftId).url

  private def changeOtherIndividualRoute(index: Int): String =
    controllers.register.individual.routes.CheckDetailsController.onPageLoad(index, fakeDraftId).url

  private lazy val addOtherIndividualRoute = routes.AddOtherIndividualController.onPageLoad(fakeDraftId).url

  private lazy val nameRoute = irts.NameController.onPageLoad(index, fakeDraftId).url

  private lazy val addOnePostRoute = routes.AddOtherIndividualController.submitOne(fakeDraftId).url

  private lazy val addAnotherPostRoute = routes.AddOtherIndividualController.submitAnother(fakeDraftId).url

  private lazy val submitCompleteRoute = routes.AddOtherIndividualController.submitComplete(fakeDraftId).url

  private val formProvider = new AddOtherIndividualFormProvider()
  private val form = formProvider()

  private val yesNoForm = new YesNoFormProvider().withPrefix("trustHasOtherIndividualYesNo")

  private lazy val otherIndividualsComplete = List(
    AddRow("Name 1", typeLabel = "Other Individual", changeOtherIndividualRoute(0), removeOtherIndividualRoute(0)),
    AddRow("Name 2", typeLabel = "Other Individual", changeOtherIndividualRoute(1), removeOtherIndividualRoute(1)),
    AddRow("Name 3", typeLabel = "Other Individual", changeOtherIndividualRoute(2), removeOtherIndividualRoute(2))
  )

  private val userAnswersWithOtherIndividualsComplete = emptyUserAnswers
    .set(TrustHasOtherIndividualYesNoPage, true).success.value
    .set(NamePage(0), FullName("Name", None, "1")).success.value
    .set(DateOfBirthYesNoPage(0), false).success.value
    .set(NationalInsuranceYesNoPage(0), true).success.value
    .set(NationalInsuranceNumberPage(0), "1234567890").success.value
    .set(OtherIndividualStatus(0), Completed).success.value
    .set(NamePage(1), FullName("Name", None, "2")).success.value
    .set(DateOfBirthYesNoPage(1), false).success.value
    .set(NationalInsuranceYesNoPage(1), true).success.value
    .set(NationalInsuranceNumberPage(1), "1234567890").success.value
    .set(OtherIndividualStatus(1), Completed).success.value
    .set(NamePage(2), FullName("Name", None, "3")).success.value
    .set(DateOfBirthYesNoPage(2), false).success.value
    .set(NationalInsuranceYesNoPage(2), true).success.value
    .set(NationalInsuranceNumberPage(2), "1234567890").success.value
    .set(OtherIndividualStatus(2), Completed).success.value

  private def genOtherIndividuals(range: Int): UserAnswers = {
    (0 until range)
      .foldLeft(emptyUserAnswers)((ua,index) =>
        ua.set(NamePage(index), FullName("First", None, "Last")).success.value
      )
  }

  "AddOtherIndividual Controller" when {

    "no data" must {

      "redirect to Session Expired for a GET if no existing data is found" in {
        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, addOtherIndividualRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }

      "redirect to Session Expired for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request =
          FakeRequest(POST, addAnotherPostRoute)
            .withFormUrlEncodedBody(("value", AddOtherIndividual.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }

    "there are no other individuals" must {

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TrustHasOtherIndividualYesNoPage, true).success.value)).build()

        val request = FakeRequest(GET, addOtherIndividualRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrustHasOtherIndividualYesNoView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, fakeDraftId)(request, messages).toString

        application.stop()
      }

      "redirect to the next page when valid data is submitted" in {

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TrustHasOtherIndividualYesNoPage, false).success.value)).build()

        val request =
          FakeRequest(POST, addOnePostRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual nameRoute

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TrustHasOtherIndividualYesNoPage, true).success.value)).build()

        val request =
          FakeRequest(POST, addOnePostRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = yesNoForm.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[TrustHasOtherIndividualYesNoView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, fakeDraftId)(request, messages).toString

        application.stop()
      }

    }

    "there are other individuals" must {

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(userAnswersWithOtherIndividualsComplete)).build()

        val request = FakeRequest(GET, addOtherIndividualRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddOtherIndividualView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, fakeDraftId, Nil, otherIndividualsComplete, "You have added 3 other individuals", false)(request, messages).toString

        application.stop()
      }

      "populate the view without value on a GET when the question has previously been answered" in {
        val userAnswers = userAnswersWithOtherIndividualsComplete
          .set(AddOtherIndividualPage, AddOtherIndividual.YesNow).success.value
          .set(TrustHasOtherIndividualYesNoPage, true).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, addOtherIndividualRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddOtherIndividualView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, fakeDraftId, Nil, otherIndividualsComplete, "You have added 3 other individuals", false)(request, messages).toString

        application.stop()
      }

      "redirect to the next page when valid data is submitted" in {

        val index =3
        val application =
          applicationBuilder(userAnswers = Some(userAnswersWithOtherIndividualsComplete)).build()

        val request =
          FakeRequest(POST, addAnotherPostRoute)
            .withFormUrlEncodedBody(("value", AddOtherIndividual.options.head.value))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual irts.NameController.onPageLoad(index, fakeDraftId).url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TrustHasOtherIndividualYesNoPage, false).success.value)).build()

        val request =
          FakeRequest(POST, addAnotherPostRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AddOtherIndividualView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, fakeDraftId, Nil, Nil, "Add other individual", false)(request, messages).toString

        application.stop()
      }

    }

    "maxed out other individuals" must {

      "return correct view when otherIndividuals is maxed out" in {

        val otherIndividuals = List(
          genOtherIndividuals(max)        )

        val userAnswers = otherIndividuals.foldLeft(emptyUserAnswers)((x, acc) => acc.copy(data = x.data.deepMerge(acc.data)))

        val application = applicationBuilder(userAnswers = Some(userAnswers.set(TrustHasOtherIndividualYesNoPage, true).success.value)).build()

        val request = FakeRequest(GET, addOtherIndividualRoute)

        val result = route(application, request).value

        contentAsString(result) must include("You cannot add another other individual as you have entered a maximum of 25.")
        contentAsString(result) must include("If you have further other individuals to add, write to HMRC with their details.")

        application.stop()
      }

      "redirect to registration progress when user clicks continue" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TrustHasOtherIndividualYesNoPage, false).success.value)).build()

        val request = FakeRequest(POST, submitCompleteRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "http://localhost:9781/trusts-registration/draftId/registration-progress"

        application.stop()

      }

    }

  }
}
