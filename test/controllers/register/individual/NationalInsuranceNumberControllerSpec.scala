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

package controllers.register.individual

import base.SpecBase
import config.annotations.OtherIndividual
import forms.NationalInsuranceNumberFormProvider
import models.FullName
import navigation.{FakeNavigator, Navigator}
import pages.register.individual.{NamePage, NationalInsuranceNumberPage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.individual.NationalInsuranceNumberView

class NationalInsuranceNumberControllerSpec extends SpecBase {

  private val formProvider = new NationalInsuranceNumberFormProvider()
  private val index: Int = 0
  private val form = formProvider.withPrefix("otherIndividual.nationalInsuranceNumber", emptyUserAnswers, index)
  private val name = FullName("first name", None, "Last name")

  lazy val otherIndividualNationalInsuranceNumberRoute: String = routes.NationalInsuranceNumberController.onPageLoad(index,draftId).url

  "NationalInsuranceNumber Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.set(NamePage(index),
        name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, otherIndividualNationalInsuranceNumberRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[NationalInsuranceNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form,name.toString, index, draftId)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(NationalInsuranceNumberPage(index), "answer").success.value
        .set(NamePage(index),name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, otherIndividualNationalInsuranceNumberRoute)

      val view = application.injector.instanceOf[NationalInsuranceNumberView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill("answer"),name.toString, index, draftId)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(NamePage(index),
        name).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].qualifiedWith(classOf[OtherIndividual]).toInstance(new FakeNavigator)
          ).build()

      val request =
        FakeRequest(POST, otherIndividualNationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", "JP123456A"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors" when {
      "invalid data is submitted" in {

        val userAnswers = emptyUserAnswers.set(NamePage(index),
          name).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request =
          FakeRequest(POST, otherIndividualNationalInsuranceNumberRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[NationalInsuranceNumberView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm,name.toString, index, draftId)(request, messages).toString

        application.stop()
      }

      "duplicate nino is submitted" in {

        val nino = "JH123456C"

        val userAnswers = emptyUserAnswers
          .set(NamePage(index), name).success.value
          .set(NationalInsuranceNumberPage(index + 1), nino).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request =
          FakeRequest(POST, otherIndividualNationalInsuranceNumberRoute)
            .withFormUrlEncodedBody(("value", nino))

        val boundForm = form
          .bind(Map("value" -> nino))
          .withError("value", "otherIndividual.nationalInsuranceNumber.error.duplicate")

        val view = application.injector.instanceOf[NationalInsuranceNumberView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm,name.toString, index, draftId)(request, messages).toString

        application.stop()
      }
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, otherIndividualNationalInsuranceNumberRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, otherIndividualNationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}
