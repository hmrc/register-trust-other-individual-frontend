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
import forms.YesNoFormProvider
import models.FullName
import navigation.{FakeNavigator, Navigator}
import pages.register.individual.{CountryOfNationalityInTheUkYesNoPage, NamePage}
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.register.individual.CountryOfNationalityInTheUkYesNoView

class CountryOfNationalityInTheUkYesNoControllerSpec extends SpecBase {

  val formProvider = new YesNoFormProvider()
  val form: Form[Boolean] = formProvider.withPrefix("otherIndividual.5mld.countryOfNationalityInTheUkYesNo")
  val index: Int = 0
  val name: FullName = FullName("FirstName", None, "LastName")

  lazy val countryOfNationalityInTheUkYesNo: String = routes.CountryOfNationalityInTheUkYesNoController.onPageLoad(index, draftId).url

  "CountryOfNationalityInTheUkYesNo Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, countryOfNationalityInTheUkYesNo)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CountryOfNationalityInTheUkYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, draftId, index, name.toString)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(NamePage(index), name).success.value
        .set(CountryOfNationalityInTheUkYesNoPage(index), true).success.value


      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, countryOfNationalityInTheUkYesNo)

      val view = application.injector.instanceOf[CountryOfNationalityInTheUkYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), draftId, index, name.toString)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(
          bind[Navigator].qualifiedWith(classOf[OtherIndividual]).toInstance(new FakeNavigator)
        ).build()

      val request =
        FakeRequest(POST, countryOfNationalityInTheUkYesNo)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(NamePage(index), name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, countryOfNationalityInTheUkYesNo)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[CountryOfNationalityInTheUkYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, draftId, index, name.toString)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, countryOfNationalityInTheUkYesNo)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, countryOfNationalityInTheUkYesNo)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}
