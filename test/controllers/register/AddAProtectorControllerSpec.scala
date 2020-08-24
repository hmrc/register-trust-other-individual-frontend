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
import forms.{AddAProtectorFormProvider, YesNoFormProvider}
import models.Status.Completed
import models.UserAnswers
import models.register.pages.AddAProtector
import pages.entitystatus.BusinessProtectorStatus
import pages.register.{AddAProtectorPage, TrustHasProtectorYesNoPage}
import pages.register.business.{NamePage, UtrPage, UtrYesNoPage}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.AddAProtectorViewHelper
import viewmodels.AddRow
import views.html.register.{AddAProtectorView, AddAProtectorYesNoView, TrustHasProtectorYesNoView}

class AddAProtectorControllerSpec extends SpecBase {

  private def onwardRoute: Call = Call("GET", "/foo")

  private val index: Int = 0
  private val max: Int = 25

  private def removeBusinessRoute(index: Int): String =
    controllers.register.business.routes.RemoveProtectorController.onPageLoad(index, fakeDraftId).url

  private def changeBusinessRoute(index: Int): String =
    controllers.register.business.routes.CheckDetailsController.onPageLoad(index, fakeDraftId).url

  private lazy val addAProtectorRoute = routes.AddAProtectorController.onPageLoad(fakeDraftId).url

  private lazy val trustHasProtectorRoute = routes.TrustHasProtectorYesNoController.onPageLoad(fakeDraftId).url

  private lazy val addOnePostRoute = routes.AddAProtectorController.submitOne(fakeDraftId).url

  private lazy val addAnotherPostRoute = routes.AddAProtectorController.submitAnother(fakeDraftId).url

  private lazy val submitCompleteRoute = routes.AddAProtectorController.submitComplete(fakeDraftId).url

  private val formProvider = new AddAProtectorFormProvider()
  private val form = formProvider()

  private val yesNoForm = new YesNoFormProvider().withPrefix("trustHasProtectorYesNo")

  private lazy val protectorsComplete = List(
    AddRow("Business Name 1", typeLabel = "Business protector", changeBusinessRoute(0), removeBusinessRoute(0)),
    AddRow("Business Name 2", typeLabel = "Business protector", changeBusinessRoute(1), removeBusinessRoute(1)),
    AddRow("Business Name 3", typeLabel = "Business protector", changeBusinessRoute(2), removeBusinessRoute(2))
  )

  private val userAnswersWithProtectorsComplete = emptyUserAnswers
    .set(TrustHasProtectorYesNoPage, true).success.value
    .set(NamePage(0), "Business Name 1").success.value
    .set(UtrYesNoPage(0), true).success.value
    .set(UtrPage(0), "1234567890").success.value
    .set(BusinessProtectorStatus(0), Completed).success.value
    .set(NamePage(1), "Business Name 2").success.value
    .set(UtrYesNoPage(1), true).success.value
    .set(UtrPage(1), "1234567890").success.value
    .set(BusinessProtectorStatus(1), Completed).success.value
    .set(NamePage(2), "Business Name 3").success.value
    .set(UtrYesNoPage(2), true).success.value
    .set(UtrPage(2), "1234567890").success.value
    .set(BusinessProtectorStatus(2), Completed).success.value

  private def genBusinessProtectors(range: Int): UserAnswers = {
    (0 until range)
      .foldLeft(emptyUserAnswers)((ua,index) =>
        ua.set(NamePage(index), "Business Name").success.value
      )
  }

  "AddAProtector Controller" when {

    "no data" must {

      "redirect to Session Expired for a GET if no existing data is found" in {
        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, addAProtectorRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }

      "redirect to Session Expired for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request =
          FakeRequest(POST, addAnotherPostRoute)
            .withFormUrlEncodedBody(("value", AddAProtector.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }

    "there are no protectors" must {

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TrustHasProtectorYesNoPage, true).success.value)).build()

        val request = FakeRequest(GET, addAProtectorRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TrustHasProtectorYesNoView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, fakeDraftId)(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to the next page when valid data is submitted" in {

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TrustHasProtectorYesNoPage, false).success.value)).build()

        val request =
          FakeRequest(POST, addOnePostRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TrustHasProtectorYesNoPage, true).success.value)).build()

        val request =
          FakeRequest(POST, addOnePostRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = yesNoForm.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[TrustHasProtectorYesNoView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, fakeDraftId)(fakeRequest, messages).toString

        application.stop()
      }

    }

    "there are protectors" must {

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(userAnswersWithProtectorsComplete)).build()

        val request = FakeRequest(GET, addAProtectorRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddAProtectorView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, fakeDraftId, Nil, protectorsComplete, "You have added 3 protectors", Nil)(fakeRequest, messages).toString

        application.stop()
      }

      "populate the view without value on a GET when the question has previously been answered" in {
        val userAnswers = userAnswersWithProtectorsComplete
          .set(AddAProtectorPage, AddAProtector.YesNow).success.value
          .set(TrustHasProtectorYesNoPage, true).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, addAProtectorRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddAProtectorView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, fakeDraftId, Nil, protectorsComplete, "You have added 3 protectors", Nil)(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to the next page when valid data is submitted" in {

        val application =
          applicationBuilder(userAnswers = Some(userAnswersWithProtectorsComplete)).build()

        val request =
          FakeRequest(POST, addAnotherPostRoute)
            .withFormUrlEncodedBody(("value", AddAProtector.options.head.value))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TrustHasProtectorYesNoPage, false).success.value)).build()

        val request =
          FakeRequest(POST, addAnotherPostRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AddAProtectorView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, fakeDraftId, Nil, Nil, "Add a protector", Nil)(fakeRequest, messages).toString

        application.stop()
      }

    }

    "maxed out protectors" must {

      "return correct view when protectors is maxed out" in {

        val protectors = List(
          genBusinessProtectors(max)        )

        val userAnswers = protectors.foldLeft(emptyUserAnswers)((x, acc) => acc.copy(data = x.data.deepMerge(acc.data)))

        val application = applicationBuilder(userAnswers = Some(userAnswers.set(TrustHasProtectorYesNoPage, true).success.value)).build()

        val request = FakeRequest(GET, addAProtectorRoute)

        val result = route(application, request).value

        contentAsString(result) must include("You cannot add another protector as you have entered a maximum of 25.")
        contentAsString(result) must include("If you have further protectors to add, write to HMRC with their details.")

        application.stop()
      }

      "redirect to registration progress when user clicks continue" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers.set(TrustHasProtectorYesNoPage, false).success.value)).build()

        val request = FakeRequest(POST, submitCompleteRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "http://localhost:9781/trusts-registration/draftId/registration-progress"

        application.stop()

      }

    }

  }
}
