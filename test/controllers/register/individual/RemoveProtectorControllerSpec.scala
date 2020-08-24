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

package controllers.register.individual

import base.SpecBase
import controllers.register.business.IndexValidation
import forms.YesNoFormProvider
import models.Status._
import models.{FullName, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import pages.entitystatus.IndividualProtectorStatus
import pages.register.individual.NamePage
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import sections.IndividualProtectorView
import views.html.RemoveIndexView

import scala.concurrent.Future

class RemoveProtectorControllerSpec extends SpecBase with IndexValidation {

  private val prefix = "removeProtector"
  private val formProvider = new YesNoFormProvider()
  private def form(prefix: String): Form[Boolean] = formProvider.withPrefix(prefix)

  private val index = 0
  private val testName = FullName("A", Some("Test"), "Name")
  private val defaultProtectorName = "the protector"

  private lazy val removeRoute: String = routes.RemoveProtectorController.onPageLoad(index, fakeDraftId).url

  "Individual RemoveProtectorController" must {

    "return OK and the correct view for a GET" when {

      "protector without name" in {

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(IndividualProtectorStatus(index), InProgress).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, removeRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[RemoveIndexView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(prefix),
            fakeDraftId,
            index,
            defaultProtectorName,
            routes.RemoveProtectorController.onSubmit(index, draftId))(fakeRequest, messages).toString

        application.stop()
      }

      "protector with name" in {

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(IndividualProtectorStatus(index), InProgress).success.value
          .set(NamePage(index), testName).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, removeRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[RemoveIndexView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(prefix),
            fakeDraftId,
            index,
            testName.toString,
            routes.RemoveProtectorController.onSubmit(index, draftId))(fakeRequest, messages).toString

        application.stop()
      }
    }

    "redirect to add to page" when {

      lazy val addToPageRoute: String = controllers.register.routes.AddAProtectorController.onPageLoad(fakeDraftId).url

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(IndividualProtectorStatus(index), Completed).success.value
        .set(NamePage(index), testName).success.value

      "YES is submitted and trustee is removed" in {

        reset(registrationsRepository)
        when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))
        when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(None))

        val application =
          applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(POST, removeRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual addToPageRoute

        val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(registrationsRepository, times(1)).set(uaCaptor.capture)(any(), any())
        uaCaptor.getValue.get(IndividualProtectorView(index)) mustNot be(defined)

        application.stop()
      }

      "NO is submitted and trustee is not removed" in {

        reset(registrationsRepository)
        when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))
        when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(None))

        val application =
          applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(POST, removeRoute)
          .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual addToPageRoute

        val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(registrationsRepository, times(0)).set(uaCaptor.capture)(any(), any())

        application.stop()
      }
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(IndividualProtectorStatus(index), Completed).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, removeRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form(prefix).bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[RemoveIndexView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(
          boundForm,
          fakeDraftId,
          index,
          defaultProtectorName,
          routes.RemoveProtectorController.onSubmit(index, draftId))(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, removeRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, removeRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}