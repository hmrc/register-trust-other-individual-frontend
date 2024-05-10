/*
 * Copyright 2024 HM Revenue & Customs
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
import forms.YesNoFormProvider
import models.TaskStatus
import org.mockito.ArgumentMatchers.{any, eq => mEq}
import org.mockito.Mockito
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import pages.register.TrustHasOtherIndividualYesNoPage
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.TrustsStoreService
import uk.gov.hmrc.http.HttpResponse
import views.html.register.TrustHasOtherIndividualYesNoView

import scala.concurrent.Future

class TrustHasOtherIndividualYesNoControllerSpec extends SpecBase with BeforeAndAfterEach {

  private val form: Form[Boolean] = new YesNoFormProvider().withPrefix("trustHasOtherIndividualYesNo")
  lazy val trustHasOtherIndividualYesNoRoute: String = routes.TrustHasOtherIndividualYesNoController.onPageLoad(draftId).url

  private val baseAnswers = emptyUserAnswers.set(TrustHasOtherIndividualYesNoPage, true).success.value

  private val mockTrustsStoreService: TrustsStoreService = Mockito.mock(classOf[TrustsStoreService])

  override def beforeEach(): Unit = {
    reset(mockTrustsStoreService)

    when(mockTrustsStoreService.updateTaskStatus(any(), any())(any(), any()))
      .thenReturn(Future.successful(HttpResponse(OK, "")))
  }

  "TrustHasOtherIndividualYesNo Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, trustHasOtherIndividualYesNoRoute)

      val view = application.injector.instanceOf[TrustHasOtherIndividualYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, draftId)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val answers = baseAnswers.set(TrustHasOtherIndividualYesNoPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, trustHasOtherIndividualYesNoRoute)

      val view = application.injector.instanceOf[TrustHasOtherIndividualYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), draftId)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" when {

      "yes selected" in {
        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(
              bind[TrustsStoreService].to(mockTrustsStoreService)
            )
            .build()

        val request =
          FakeRequest(POST, trustHasOtherIndividualYesNoRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.InfoController.onPageLoad(draftId).url

        verify(mockTrustsStoreService).updateTaskStatus(mEq(draftId), mEq(TaskStatus.InProgress))(any(), any())

        application.stop()
      }

      "no selected" in {
        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(
              bind[TrustsStoreService].to(mockTrustsStoreService)
            )
            .build()

        val request =
          FakeRequest(POST, trustHasOtherIndividualYesNoRoute)
            .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "http://localhost:9781/trusts-registration/draftId/registration-progress"

        verify(mockTrustsStoreService).updateTaskStatus(mEq(draftId), mEq(TaskStatus.Completed))(any(), any())

        application.stop()
      }

    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request = FakeRequest(POST, trustHasOtherIndividualYesNoRoute)

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[TrustHasOtherIndividualYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, draftId)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, trustHasOtherIndividualYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, trustHasOtherIndividualYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}
